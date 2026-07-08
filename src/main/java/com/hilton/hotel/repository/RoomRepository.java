package com.hilton.hotel.repository;

import com.hilton.hotel.domain.Room;
import com.hilton.hotel.domain.RoomStatus;
import com.hilton.hotel.domain.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    boolean existsByRoomNumber(String roomNumber);

    Page<Room> findByStatus(RoomStatus status, Pageable pageable);

    List<Room> findByType(RoomType type);

    List<Room> findByStatusAndType(RoomStatus status, RoomType type);

    Page<Room> findByRoomNumberContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String roomNumber,
            String description,
            Pageable pageable
    );

    @Query("""
        SELECT r
        FROM Room r
        WHERE LOWER(r.roomNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(r.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(CAST(r.type AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(CAST(r.status AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Page<Room> searchRooms(
            @Param("keyword") String keyword,
            Pageable pageable
    );
}