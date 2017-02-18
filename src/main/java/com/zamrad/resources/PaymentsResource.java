package com.zamrad.resources;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments/v1")
@CrossOrigin
@Api(value = "/payments", description = "Manage event payments.")
public class PaymentsResource {
    //Create a payment by supplying client token and a valid event slot id
    //Create a payments entry associated with that id and associate some sort of status with it
}
