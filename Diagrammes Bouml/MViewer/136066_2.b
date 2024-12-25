class ExampleInstrumentedTest
!!!137474.java!!!	useAppContext() : void
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("fr.red.mviewer", appContext.getPackageName());
