package net._95point2.thingping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThingPingTests {

	@Test
	public void testClientURL() {
		assertThat(
				ThingPing.clientBuilder()
					.withAccount("ACCOUNT1").withThingId("THING1")
					.withInterval("daily").withNotify("rgs@example.local")
					.build()
				.url.toString())
			.isEqualTo("http://thingping.net/v1/ping?account=ACCOUNT1&thing=THING1&freq=daily&notify=rgs%40example.local");
		
		assertThat(
					ThingPing.clientBuilder()
					.withAccount("ACCOUNT1").withThingId("THING1")
					.build()
				.url.toString())
			.isEqualTo("http://thingping.net/v1/ping?account=ACCOUNT1&thing=THING1");
		
		try {
			ThingPing.clientBuilder().build();
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch(IllegalArgumentException iae){
			assertThat(iae).hasMessage("no accountId provided");
		}
		
		try {
			ThingPing.clientBuilder().withAccount("ACCOUNT1").build();
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch(IllegalArgumentException iae){
			assertThat(iae).hasMessage("no thingId provided");
		}
		
		try {
			ThingPing.clientBuilder().withAccount("ACCOUNT1").withThingId("THING1").withInterval("monthly").build();
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch(IllegalArgumentException iae){
			assertThat(iae).hasMessage("interval is not valid: monthly");
		}
		
		try {
			ThingPing.clientBuilder().withAccount("ACCOUNT1").withThingId("THING1").withInterval("PT10D").build();
			failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
		} catch(IllegalArgumentException iae){
			assertThat(iae).hasMessage("interval is not valid: PT10D");
		}
	}
	
	@Test
	public void testClientCall() {
		ThingPing client = ThingPing.clientBuilder()
				.withUrlBase("https://ptsv2.com/t/64p19-1541114420/post") // https://ptsv2.com/t/64p19-1541114420
			.withAccount("ACCOUNT1").withThingId("THING1")
			.withInterval("daily").withNotify("rgs@example.local")
			.build();
		
		log.warn("URL: {}", client.url.toString());
		
		client.ping(true);
	}
}
