plugins {
    application
}

dependencies {
    implementation(project(":main"))
    runtimeOnly("ch.qos.logback:logback-classic:1.2.11")
    testImplementation(kotlin("test"))
}

application {
    mainClass.set("com.pattexpattex.dikko.testbot.BotKt")
}
