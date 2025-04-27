//package com.example.hotel.BookingService.Controllers;
//
//import com.example.hotel.BookingService.Services.BookingService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/bookings")
//public class BookingController {
//    private final BookingService service;
//
//    @Autowired
//    public BookingController(BookingService service) {
//        this.service = service;
//    }
//
//    @PostMapping
//    public ResponseEntity<String> book(@RequestParam String roomType, @RequestParam int nights) {
//    String id = service.createBooking(roomType, nights);//+"Hana_Younis_52_25989";
//        return ResponseEntity.ok(id);
//    }
//}
//
