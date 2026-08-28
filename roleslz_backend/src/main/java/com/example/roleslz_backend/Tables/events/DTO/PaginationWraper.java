package com.example.roleslz_backend.Tables.events.DTO;

import java.util.List;

public record PaginationWraper(
        List<EventDTOSumary> content,
        int currentPage,
        long totalItems,
        long totalPages
) {}
