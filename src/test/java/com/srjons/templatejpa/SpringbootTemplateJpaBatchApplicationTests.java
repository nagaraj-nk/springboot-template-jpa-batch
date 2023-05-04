package com.srjons.templatejpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

class SpringbootTemplateJpaBatchApplicationTests {

	public static void main(String[] args) {
		for (int i = 0; i < 100000; i++) {
			System.out.println(UUID.randomUUID().toString() + "," + UUID.randomUUID().toString()
			+"," +UUID.randomUUID().toString() + "," +UUID.randomUUID().toString() + "@somedomain.com");
		}
	}
}
