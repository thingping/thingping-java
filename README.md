# ThingPing

This is the Java client for the ThingPing service. 

ThingPing.net is a simple service that provides notifications when things stop working.
It's easy to setup and can be used to help check that everything you build continues to run.

Try it for free at http://thingping.net

[ ![Download](https://api.bintray.com/packages/thingping/thingping-client-java/thingping-java/images/download.svg) ](https://bintray.com/thingping/thingping-client-java/thingping-java/_latestVersion)

## Usage

**Create the client.**

    ThingPing client = ThingPing.clientBuilder()
				.withAccount("ACCOUNT1")         // your accountId  
				.withThingId("THING1")           // thing thingId
				.withInterval("daily")           // "daily", "hourly" or ISO Duration: "PT3H"
				.withNotify("rgs@example.local") // your email
				.build();

**Send a notification whenever you need to.**

    try
    {
        client.ping(true);
    }
    catch(ThingPingException tpe) 
    {
        log.warn("ThingPing notify error: {}", tpe.getMessage(), tpe);
    }

*If you don't care about exceptions, you can call `ping()` which swallows all exceptions.*

    client.ping()
