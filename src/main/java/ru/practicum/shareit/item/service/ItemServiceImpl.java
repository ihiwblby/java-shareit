package ru.practicum.shareit.item.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dal.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dal.CommentRepository;
import ru.practicum.shareit.item.dal.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ItemServiceImpl implements ItemService {

    ItemRepository itemRepository;
    UserRepository userRepository;
    BookingRepository bookingRepository;
    CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(owner);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + id + " не найдена"));

        List<CommentDto> comments = commentRepository.findByItem(item).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemDto itemDto = ItemMapper.toItemDto(item);
        setBookingsForOneItem(itemDto);
        itemDto.setComments(comments);
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemListResponseDto> getAllByOwner(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        return itemRepository.findByOwnerId(userId).stream()
                .map(ItemMapper::toItemListResponseDto)
                .toList();
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь c ID = " + userId + " не найден"));

        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + itemId + " не найдена"));

        if (!oldItem.getOwner().equals(owner)) {
            throw new ConditionsNotMetException("Редактировать вещь может только её владелец");
        }

        if (itemDto.getName() != null) {
            oldItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            oldItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            oldItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(oldItem));
    }

    @Override
    public void delete(Long id) {
        itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + id + " не найдена"));

        itemRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String normalizedText = text.toLowerCase();
        return itemRepository.search(normalizedText).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, CommentDto commentDto, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с ID = " + itemId + " не найдена"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));

        boolean isBooking = bookingRepository.findByBookerAndItem(author, item).stream()
                .anyMatch(booking ->
                        booking.getStatus() == BookingStatus.APPROVED
                                && booking.getEnd().isBefore(LocalDateTime.now()));
        if (!isBooking) {
            throw new ConditionsNotMetException("Пользователь не бронировал эту вещь или срок бронирования ещё не истёк");
        }

        Comment comment = CommentMapper.toComment(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return CommentMapper.toCommentDto(savedComment);
    }

    private void setBookingsForOneItem(ItemDto itemDto) {
        LocalDateTime now = LocalDateTime.now();
        Item item = ItemMapper.toItem(itemDto);

        List<Booking> approvedBookings = bookingRepository.findAllApprovedByItem(item,
                Sort.by(Sort.Direction.DESC, "start"));

        approvedBookings.stream()
                .filter(booking ->
                        (booking.getStart().isBefore(now) || booking.getStart().isEqual(now)) &&
                                booking.getEnd().isAfter(now))
                .findFirst()
                .ifPresentOrElse(
                        currentBooking -> itemDto.setLastBooking(BookingMapper.toBookingDto(currentBooking)),
                        () -> itemDto.setLastBooking(null)
                );

        approvedBookings.stream()
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .ifPresent(nextBooking -> itemDto.setNextBooking(BookingMapper.toBookingDto(nextBooking)));
    }
}