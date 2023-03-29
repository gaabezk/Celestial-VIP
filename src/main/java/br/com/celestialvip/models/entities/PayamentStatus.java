package br.com.celestialvip.models.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayamentStatus {
    private String paymentId;
    private String status;
    private String externalReference;

    @Override
    public String toString() {
        return "Payment ID: " + paymentId + " | Status: " + status + " | External Reference: " + externalReference;
    }
}