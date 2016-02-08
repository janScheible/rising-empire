# Rising Empire
This project has multiple sub-goals:
- leveraging the power of Spring Boot
  - Under the hood a regular form based Spring Security login is used
  - WebSocket communication is protected by Spring Security as well (even session timeout works thanks to Spring Session)
- providing an as convenient as possible development experience with Netbeans
  - Unit Tests are only executed if explicitly chosen
  - Spring Boot JAR files are not created when only debugging
  - HTML templates and TypeScript files are updated without an application restart
- implementation of a clean architecture with a framework independent game core
- ... and finaly creation of a game :-)
 
I called it a framework because everything is there: The game core, AI support, WebSocket communication and a Kendo UI GUI. But what's currently missing is game logic. The only thing a human player and the AI is able to do is sending a single fleet to the stars of the universe. But doesn't that already sound cool? ;-)

![Join](https://github.com/janScheible/rising-empire/blob/master/join.png)
![Game](https://github.com/janScheible/rising-empire/blob/master/game.png)

## Prerequisites
1. recent JDK 8
1. Netbeans >= 8.0
1. [Netbeans Typescript Plugin](https://github.com/Everlaw/nbts)
  - If you have `tsc.cmd` on the path you don't even have to set `application.typescript.path` in application.properties
1. Recent Browser: works best in Chrome 48; Firefox 43 and IE 11 work as well but currently suffer from [#2](/../../issues/2)

All you have to do to lunch the game is open the project in Netbeans, right click and debug.

## Architecture
- com.scheible.risingempire
  - game
    - <dl><dt>core</dt>
        <dd>Contains `Universe` as the root object. The universe represents everything. There are no view restrictions or anything. In the core there is no notion of game or player. There are only stars, fleets and leaders of nations.</dd></dl>
    - <dl><dt>common</dt>
        <dd>`Game` introduces the concept of a game. There a players that only see a part of the universe.</dd></dl>
    - <dl><dt>ai</dt>
        <dd>The AI implementations only have access to the common view. That means they can't cheat and only see what a human player would see.</dd></dl>
  - web
    - <dl><dt>config</dt>
        <dd>Contains the security and JSON config for regular HTPP and WebSockets. The WebSockets configuration itself and finally the implementation of the embedded TypeScript support and the embedded Redis support for Spring Session (which is used to also have session expiration for WebSockets).</dd></dl>
    - <dl><dt>security</dt>
        <dd>An implementation of Spring Security's `UserDetailsService` and `ConnectedPlayer` as the implementation of `UserDetails`.</dd></dl>
    - <dl><dt>appearance</dt>
        <dd>Very basic implementation of game resources. Currently the list of available players and their color.</dd></dl>
    - <dl><dt>join</dt>
        <dd>Controllers and messaging POJOs for the join page.</dd></dl>
    - <dl><dt>game</dt>
        <dd>Controllers and messaging POJOs for the game page. `GameHolder` is the glue between the Spring world and the game. Currently a new game is started at server boot and can't be terminated or restarted. `GameHolder` makes heavy use of `synchronized` to avoid trouble in the multi threaded world of a HTTP server.</dd></dl>
