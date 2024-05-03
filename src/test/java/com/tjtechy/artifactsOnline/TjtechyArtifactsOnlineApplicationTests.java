package com.tjtechy.artifactsOnline;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles(value = "development") //only used for test case class override any active profile defined in the application.ym file
class TjtechyArtifactsOnlineApplicationTests {

	@Test
	void contextLoads() {
	}

}
