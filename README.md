# Dikko

Library for creating or prototyping Discord bots quickly and efficiently.
Built on top of [JDA](https://github.com/DV8FromTheWorld/JDA) and [jda-ktx](https://github.com/MinnDevelopment/jda-ktx).

To get the most out of this library, also read the [jda-ktx readme](https://github.com/MinnDevelopment/jda-ktx#readme).

## Download

### Gradle

```kotlin
repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("com.github.PattexPattex:Dikko:${COMMIT}")
}
```

## Examples

### Basic

```kotlin
fun main(args: Array<String>) {
    // Create instance of JDA
    val jda = default(args[0], enableCoroutines = true) { // Dikko needs coroutines enabled
        intents += GatewayIntent.GUILD_MEMBERS
        /* ... */
    }
    
    Dikko.create(jda, "com.example.bot.commands") {
        // Add extra packages
        packages += "com.example.bot.another.package"

        // Add extra event handlers, outside configured packages
        additionalEventHandlers += HelpCommand::class // Default /help command included in Dikko
        
        // Configure replies when things go wrong
        failureMessages {
            unknownError = { "Something went wrong 😢" }
            invocationException = { t: Throwable -> t.message!! } // Called when handler throws
        }
    }
}

// Handlers must be in a class or an object
// Handlers cannot be top-level functions
class Handlers {
    // Define command structure
    @Definition("foo")
    val foo = slash("foo", "Lorem ipsum dolor sit amet") // slash() is a part of jda-ktx

    // Define event handler
    @EventHandler("foo")
    fun foo(event: SlashEventWrapper) { // Handler for slash command '/foo'
        /* Handle the event */
    }

    // Handlers can return objects
    @EventHandler("far")
    fun far(event: SlashEventWrapper): RestAction<InteractionHook> {
        return event.reply_("far") // This RestAction will be queued by Dikko automatically
    }
    
    // Handlers can be suspending
    @EventHandler("far")
    suspend fun bar(event: SlashEventWrapper) {
        delay(5.seconds) // Suspending call
        /* ... */
    }
}
```

### Slash commands

```kotlin
// Example with options
@Definition("echo")
val echo = slash("echo", "Repeats after you") {
    option<String>("content", "What to repeat", required = true)
}

@EventHandler("echo")
fun echo(event: SlashEventWrapper, content: String) { // Automatic argument injection
    event.reply_("_$content_").queue()
}

// Optional command options
@Definition("skip")
val skip = slash("skip", "Skip track") {
    option<Int>("position", "Position to skip to", required = false)
}

@EventHandler("skip")
fun skip(event: SlashEventWrapper, position: Int?) { // Nullable parameter
    if (position == null) { // True if user did not specify a position
        nextTrack()
    } else {
        skipTo(position)
    }
    
    /* ... */
}

@EventHandler("skip")
fun skip(event: SlashEventWrapper, position: Int = 1) { // Default parameter value
    skipTo(position)
    /* ... */
}

// Example with subcommands
@Definition("command")
val subs = slash("command", "A command") {
    group("group", "A subcommand group") {
        subcommand("subcommand", "A subcommand in a group")
    }

    subcommand("subcommand", "A subcommand")
}

@EventHandler("command/group/subcommand") // Handle the subcommand in the group
fun subcommandGroup(event: SlashEventWrapper) { /* ... */ }

@EventHandler("command/subcommand") // Handle the subcommand at the base of the slash command
fun subcommand(event: SlashEventWrapper) { /* ... */ }

// Slash command groups
// Useful for help menus
@Definition("fun")
val funGroup = group(id = "fun", name = "Fun", emoji = "🪀".toEmoji(), description = "Fun and games")

@Definition("tictactoe")
val ticTacToe = slash("tictactoe", "Start a game of tic tac toe", group = "fun") // Add a command to a group with the id
```

### Autocomplete

```kotlin
@Definition("playlist")
val cmd = slash("playlist", "Play a playlist") {
    option<String>("name", "Name of playlist", required = true, autocomplete = true)
}

@EventHandler("playlist/name") // Path to the option
fun playlistName(event: AutocompleteEventWrapper, query: AutoCompleteQuery) { // 2nd parameter is optional
    val choices = getChoices(query) // Do something with the query
    event.replyChoice(choices).queue()
}

@EventHandler("playlist")
fun playlist(event: SlashEventWrapper, name: String) { // Name is autocompleted
    /* ... */
}
```

### Buttons

```kotlin
@Definition("option:yes") // Note: button definitions are optional
val yes = button("option:yes", "Yes", style = ButtonStyle.SUCCESS) // button() is from jda-ktx

@Definition("option:no")
val no = button("option:no", "No", stype = ButtonStyle.DANGER)

@EventHandler("option:{choice}") // Support for path parameters
fun hmm(event: ButtonEventWrapper) {
    println(event.path.parameters["choice"]!!.value) // Prints "yes", "no" or something else
}

@EventHandler("question:{id}.{op}") // Split parameters with dots or colons
fun hmm(event: ButtonEventWrapper) { /* ... */ }

@EventHandler("ticket-{id}_delete") // Paths can contain lowercase letters, numbers, dots, colons, hyphens and underscores
fun deleteTicket(event: ButtonEventWrapper) { /* ... */ }
```

### Selection menus

```kotlin
// Entity select menu support
@Definition("mention")
val entitySelect = entitySelect("mention", types = listOf(EntitySelectMenu.SelectTarget.USER))

@EventHandler("mention")
fun mention(event: EntitySelectEventWrapper, values: List<IMentionable>) { // The second parameter is optional
    event.reply(values.joinToString { it.asMention }).queue()
}

// String select menu support
@Definition("game")
val stringSelect = stringSelect("game") {
    option("GTA V", "gtav")
    option("Minecraft", "mc")
}

@EventHandler("game")
fun games(event: StringSelectEventWrapper, values: List<SelectOption>) { // Note the different list item type
    event.reply(values[0].label).queue()
}
```

### Modals

```kotlin
// Modal definitions are required
@Definition("modal")
val aModal = modal("modal", "Survey") {
    short("one", "A text input", required = true)
    paragraph("two", "A long text input", required = false)
}

@EventHandler("modal")
fun handler(event: ModalEventWrapper, one: ModalMapping, two: ModalMapping) { // Automatic argument injection
    /* ... */
}

// Additional parameters can be omitted
@EventHandler("modal")
fun handler(event: ModalEventWrapper) { /* ... */ }
```

### Context menus

```kotlin
// Message context menu
@Definition("save")
val msg = messageContext("save")

@EventHandler("save")
fun msg(event: MessageContextMenuEventWrapper, target: Message) { // 2nd parameter is optional
    /* ... */
}

// User context menu
@Definition("mute")
val user = userContext("mute")

@EventHandler("mue")
fun user(event: UserContextMenuEventWrapper, target: User) { // Note the different type
    /* ... */
}
```

### Annotations

```kotlin
// Exclude functions and classes annotated by @Ignore
@Ignore
@EventHandler("baz")
fun baz(event: SlashEventWrapper) { /* ... */ }

@Ignore
class SlashCommands { /* ... */ }

// Call functions annotated by @AfterSetup after initial setup
@AfterSetup
fun action() { /* ... */ }

// Functions annotated by @GuildOnly or @PrivateOnly will be called only when event is from a guild or a DM respectively.
@GuildOnly
@EventHandler("guildonly")
fun guildOnly(event: SlashEventWrapper) {
    event.guild!! // Never throws a NPE
}

@PrivateOnly
@EventHandler("privateonly")
fun privateOnly(event: SlashEventWrapper) {
    event.guild!! // Always throws
}

// @RequirePermissions and @RequireUserPermissions enable automatic permission checks of the bot's and user's permissions respectively.
@RequirePermissions(Permission.MESSAGE_EXT_EMOJI, Permission.MESSAGE_ATTACH_FILES) // Bot requires these permissions
@EventHandler("send")
fun sendImage(event: SlashEventWrapper) {
    /* ... */
}

@RequireUserPermissions(Permission.ADMINISTRATOR) // User must have these permissions
@EventHandler("admin")
fun admin(event: SlashEventWrapper) {
    /* ... */
}
```

### Creating instances of event handlers

```kotlin
class SlashCommands { // Constructor with no arguments
    /* ... */
}

object SlashCommands { // An object instead of a class 
    /* ... */
}

class SlashCommands(val dikko: Dikko) { // Constructor accepts instance of Dikko
    /* ... */
}

@UseFactory(Factory::class) // With a custom factory
class SlashCommands(val dikko: Dikko, val string: String) {
    /* ... */
    
    class Factory : EventHandlerInstanceFactory { // Custom factory, must have a no arguments constructor
        override fun createInstance(dikko: Dikko): Any = SlashCommands(dikko, "😲")
    }
}
```

### Getting instances of other event handlers

```kotlin
class ThisClass {
    @EventHandler("command")
    fun command(event: SlashEventWrapper) {
        // Via GuildContext
        // GuildContext.getHandler() throws if it cannot create an instance of ThatClass
        val button = event.ctx /* GuildContext */
            .getHandler<ThatClass>() /* ThatClass */
            .button /* Button */
        
        // Via EventDispatcher
        val button = event.getDispatcher<EventDispatcher<Button>>() /* EventDispatcher<Button>? */
            ?.proxies /* Map<String, DefinitionProxy<Button>>? */
            ?.get("btn") /* DefinitionProxy<Button>? */
            ?.value /* Button? */
        
        event.reply_(components = button.into()).queue()
    }
}

class ThatClass {
    @Definition("btn")
    val button = button("btn", "A button")
}
```

### Utilities

#### Commands

```kotlin
Dikko.create(jda, "com.example.bot.commands") {
    // Add /help and /shutdown
    additionalEventHandlers += listOf(HelpCommand::class, ShutdownCommand::class)
    
    // Configure help
    HelpCommand.configure {
        color = 0xFFFFFF
        useSelectionMenu = false
        links["Youtube"] = "https://youtu.be/dQw4w9WgXcQ"
    }
    
    // Configure shutdown
    ShutdownCommand.configure {
        ownerId = /* Your user ID */
    }
}
```

#### Listeners

```kotlin
// Listener with callback
dikko.listener<ButtonEventWrapper>("button.{op}") { event /* ButtonEventWrapper */ ->
    /* ... */
}

// Suspending await listener
val event /* : ButtonEventWrapper */ = dikko.await<ButtonEventWrapepr>("button.{op}")

// Alternative
// Note: Events that are not supported by Dikko are not supported here
jda.listener<ButtonInteractionEvent>("button.{op}") { (event /* ButtonInteractionEvent */, path) ->
    /* ... */
}

val (event, path) = jda.await<ButtonInteractionEvent>("button.{op}") // Returns Pair<ButtonInteractionEvent, Path>
```

#### Paginations

```kotlin
@EventHandler("help")
suspend fun help(event: SlashEventWrapper) {
    // Create a pagination
    val pagination = pagination {
        // Set starting page to 1 (first page has index 0)
        startingPage = 1
        
        // Set a filter
        filter {
            it.member!!.hasPermission(Permission.ADMINISTRATOR)
        }

        // Add pages
        pages {
            + MessageEdit("first page") // MessageEdit() is a part of jda-ktx
            + MessageEdit("second page")
            + MessageEdit("third page")
        }
    }.build(event) // Automatically respond to the event

    // Turn to a page
    pagination.selectPage(2)
    
    // Time out the pagination
    pagination.timeout()
    
    // Delete the pagination
    // timeout() and delete() cause result.await() and channel.receive() to throw
    pagination.delete()
    
    // Asynchronously receive events when users interact with the pagination
    // Channel is closed when pagination is deleted by a user
    for (changeEvent in pagination.channel) { // pagination.channel is ReceiveChannel<ButtonInteractionEvent>
        /* ... */
    }
    
    // Asynchronously await deletion of pagination
    val result = pagination.result.await()
    /* ... */
}
```

#### Prompts

```kotlin
@EventHandler("skip")
fun skip(event: SlashEventWrapper) {
    val connectedChannel = /* ... */
    
    val prompt = propt("Skip this track?") {
        // Set a timeout
        timeout = 1.minutes
        
        // Set a filter
        filter {
            it.member in connectedChannel.members
        }
        
        // Add prompt options
        options {
            option(
                "yes",
                text = "Yes",
                style = ButtonStyle.SUCCESS,
                emoji = "✅".toEmoji(),
                requiredSelects = connectedChannel.members.size - 2
            )
            cancel() // Only the user who created this interaction can cancel
        }
    }.build(event)
    
    // Select an option
    prompt.selectOption("yes")

    // Time out the prompt
    prompt.timeout()
    
    // Cancel the prompt
    // timeout() and cancel() cause result.await() and channel.receive() to throw
    prompt.cancel()

    // Asynchronously receive events when users interact with the prompt
    // Channel is closed when pagination is completed or cancelled by a user
    for (result in prompt.channel) {
        // Event of a user responding to the prompt
        val event = result.event // Event is null if selectOption() was used

        // Option that was selected
        val option = result.option
        
        // Type of the result
        // Can be RESPONSE or CANCEL
        val type = result.type
    }
    
    // Asynchronously await the event that completed the prompt
    // Result throws if the pagination is cancelled
    val lastInput = prompt.result.await()
}
```

#### Timeouts

```kotlin
// Create a TimeoutManager
val timeoutManager = TimeoutManager()

// Create a Timeout
val timeout = timeoutManager.create(
    "run", // A reference to this timeout
    timeout = 1.minutes, // Duration
    start = false, // Start this timeout on creation
    runPrevious = true, // If there is already a timeout with the same id, skip the wait and run it immediately
    scope = getDefaultScope() // a CoroutineScope
) {
    // This will run after 1 minute passes
    runTheThing()
}

// Create a MessageTimeout
// It will automatically disable all components of a message
val messageTimeout = timeoutManager.create("message", event.message)

// Start the timeout
timeout.start()

// Restart the timeout
// No effect if cancelled or completed
timeout.restart()

// Waits until completion of the timeout
// Throws if timeout is cancelled
timeout.await()

// Suspending function that immediately completes the timeout
timeout.runNow()

// Immediately complete the timeout, returns a Deferred
timeout.runNowAsync()

// Cancel the timeout
timeout.cancel()

// TimeoutManager has the same methods
timeoutManager.await("run") // Get a timeout by its id, then call await()
timeoutManager.runNowAll() // Run all timeouts

// Get a timeout
timeoutManager["run"]

timeoutManager.get<MessageTimeout>("message") // Get and cast the timeout

// Get a message from a MessageTimeout
val msg /* : RestAction<Message> */ = messageTimeout.message

val id /* : String */ = messageTimeout.messageId
```