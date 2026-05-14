package io.agentgrid.coworksimple.booking.domain;

// Liste fermée des statuts possibles pour éviter les valeurs libres en base et dans l'API.
public enum BookingStatus {
    CONFIRMED,
    CANCELLED
}
