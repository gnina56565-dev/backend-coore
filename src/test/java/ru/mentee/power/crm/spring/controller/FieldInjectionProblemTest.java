package ru.mentee.power.crm.spring.controller;

import org.junit.jupiter.api.Test;

class FieldInjectionProblemTest {

	@Test
	void fieldInjectionCausesNullPointerWithoutSpring() {
		DemoController controller = new DemoController(null);
		controller.demo();
	}
}
