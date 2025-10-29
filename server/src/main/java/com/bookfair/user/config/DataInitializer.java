package com.bookfair.user.config;

import com.bookfair.user.model.Business;
import com.bookfair.user.model.Stall;
import com.bookfair.user.repository.BusinessRepository;
import com.bookfair.user.repository.StallRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initStalls(StallRepository stallRepository) {
        return args -> {
            if (stallRepository.count() == 0) {
                List<Stall> stalls = List.of(
                        // ----------------------------------------------------
                        // ZONE A: Premium/Large Stalls (Higher Price, Central)
                        // ----------------------------------------------------
                        createStall("A01", "International Publisher", new BigDecimal("75000.00"), false, "10.1,20.1"),
                        createStall("A02", "International Publisher", new BigDecimal("75000.00"), false, "10.2,20.2"),
                        createStall("A03", "Local Major Publisher", new BigDecimal("65000.00"), false, "10.3,20.3"),
                        createStall("A04", "Local Major Publisher", new BigDecimal("65000.00"), false, "10.4,20.4"),
                        createStall("A05", "Educational/Textbook", new BigDecimal("60000.00"), false, "10.5,20.5"),

                        // ----------------------------------------------------
                        // ZONE B: Standard Stalls (Mid-Price, Main Aisles)
                        // ----------------------------------------------------
                        createStall("B01", "Fiction/Literature", new BigDecimal("50000.00"), false, "15.1,25.1"),
                        createStall("B02", "Fiction/Literature", new BigDecimal("50000.00"), false, "15.2,25.2"),
                        createStall("B03", "Children's Books", new BigDecimal("45000.00"), false, "15.3,25.3"),
                        createStall("B04", "Children's Books", new BigDecimal("45000.00"), false, "15.4,25.4"),
                        createStall("B05", "Non-Fiction/Biography", new BigDecimal("50000.00"), false, "15.5,25.5"),
                        createStall("B06", "Non-Fiction/Biography", new BigDecimal("50000.00"), false, "15.6,25.6"),
                        createStall("B07", "Stationery/Gifts", new BigDecimal("40000.00"), false, "15.7,25.7"),
                        createStall("B08", "Stationery/Gifts", new BigDecimal("40000.00"), false, "15.8,25.8"),
                        createStall("B09", "Religious/Spiritual", new BigDecimal("40000.00"), false, "15.9,25.9"),
                        createStall("B10", "Magazines/Periodicals", new BigDecimal("35000.00"), false, "16.0,26.0"),

                        // ----------------------------------------------------
                        // ZONE C: Small/Economy Stalls (Lower Price, Perimeter)
                        // ----------------------------------------------------
                        createStall("C01", "Used Books/Discounts", new BigDecimal("30000.00"), false, "20.1,30.1"),
                        createStall("C02", "Used Books/Discounts", new BigDecimal("30000.00"), false, "20.2,30.2"),
                        createStall("C03", "Independent Author", new BigDecimal("25000.00"), false, "20.3,30.3"),
                        createStall("C04", "Independent Author", new BigDecimal("25000.00"), false, "20.4,30.4"),
                        createStall("C05", "Academic Journals", new BigDecimal("35000.00"), false, "20.5,30.5")
                );

                stallRepository.saveAll(stalls);
                System.out.println("✅ Initialized available stalls into DB.");
            }
        };
    }

    @Bean
    CommandLineRunner initBusinesses(BusinessRepository businessRepository) {
        return args -> {
            if (businessRepository.count() == 0) {
                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                List<Business> businesses = List.of(
                        // *** Original Sri Lankan Publishers ***
                        createBusiness("Sarasavi Publishers", "SAR-001", "0112233445", "Colombo", encoder.encode("SARASAVI2025")),
                        createBusiness("MD Gunasena", "MDG-002", "0114567890", "Colombo", encoder.encode("MDG2025")),
                        createBusiness("Vijitha Yapa", "VY-003", "0117778899", "Kandy", encoder.encode("VY2025")),
                        createBusiness("Lake House", "LH-004", "0111234567", "Colombo", encoder.encode("LH2025")),

                        // ----------------------------------------------------------------------------------
                        // *** Additional Sri Lankan Publishers (Local Participation) ***
                        // ----------------------------------------------------------------------------------
                        createBusiness("Godage International Publishers", "GIP-005", "0112456789", "Maradana", encoder.encode("GODAGE2025")),
                        createBusiness("Samayawardhana Bookshop", "SMB-006", "0115678901", "Colombo", encoder.encode("SAMAYA2025")),
                        createBusiness("Fast Publishing (Pvt) Ltd", "FP-007", "0113334455", "Piliyandala", encoder.encode("FASTP2025")),
                        createBusiness("Dayawansa Jayakody & Co", "DJC-008", "0118901234", "Colombo", encoder.encode("DJC2025")),
                        createBusiness("Kandurata Publishers", "KP-009", "0812223344", "Kandy", encoder.encode("KANDU2025")),
                        createBusiness("Thakshila Publishers", "TP-010", "0112789012", "Nugegoda", encoder.encode("THAK2025")),
                        createBusiness("Pahan Publishers", "PH-011", "0114000111", "Maharagama", encoder.encode("PAHAN2025")),
                        createBusiness("Visidunu Publishers", "VP-012", "0112123123", "Colombo", encoder.encode("VISI2025")),
                        createBusiness("Mahaena Publishers", "MEP-013", "0112987654", "Galle", encoder.encode("MAHAENA2025")),
                        createBusiness("A-Z Publishers (English Focus)", "AZP-014", "0113456789", "Colombo", encoder.encode("AZP2025")),

                        // ----------------------------------------------------------------------------------
                        // *** International Publishers (Global Participation) ***
                        // ----------------------------------------------------------------------------------
                        createBusiness("Penguin Random House", "PRH-015", "0117001002", "New York", encoder.encode("PRH2025")),
                        createBusiness("Oxford University Press (OUP)", "OUP-016", "0117002003", "Oxford", encoder.encode("OUP2025")),
                        createBusiness("Cambridge University Press (CUP)", "CUP-017", "0117003004", "Cambridge", encoder.encode("CUP2025")),
                        createBusiness("HarperCollins Publishers", "HC-018", "0117004005", "New York", encoder.encode("HC2025")),
                        createBusiness("Scholastic Corporation", "SC-019", "0117005006", "New York", encoder.encode("SCHO2025")),
                        createBusiness("Hachette Livre", "HL-020", "0117006007", "Paris", encoder.encode("HAC2025"))
                );

                businessRepository.saveAll(businesses);
                System.out.println("✅ Initialized verified publishers into DB.");
            }
        };
    }

    private Business createBusiness(String name, String regNo, String phone, String address, String inviteCodeHash) {
        Business b = new Business();
        b.setName(name);
        b.setRegistrationNumber(regNo);
        b.setContactNumber(phone);
        b.setAddress(address);
        b.setInviteCodeHash(inviteCodeHash);
        b.setVerified(true);
        b.setCreatedAt(LocalDateTime.now());
        return b;
    }

    private Stall createStall(String stallCode, String category, BigDecimal price, Boolean isReserved, String locationCoordinates) {
        Stall s = new Stall();
        s.setStallCode(stallCode);
        s.setCategory(category);
        s.setPrice(price);
        s.setReserved(isReserved);
        s.setLocationCoordinates(locationCoordinates);
        return s;
    }
}
