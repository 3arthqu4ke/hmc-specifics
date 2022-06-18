## HMC-Specifics
Version specific implementations of the [HeadlessMc](https://github.com/3arthqu4ke/HeadlessMc) runtime. Just place the
jar for your version in your mods folder. Then you need to find a way to send commands to your running Minecraft game.
The easiest way is probably to just launch it with HeadlessMc.

| Name        | Description | Args/Flags  |
| ----------- | ----------- | ----------- |
| gui | Lists all currently displayed gui elements. |  |
| click | Clicks an element on Minecrafts screen. | \<id/p\> \<x\> \<y\> \<button\> -enable |
| text | Sets the contents of a text field. | \<id\> \<text\> |
| msg | Sends a chat message. | \<message\> |
| menu | Opens the ingame menu. | |
| quit | Quits the game. | |
| render | Dumps all strings rendered by Minecrafts FontRenderer. | \<time\> -f -t |
| close | Closes the menu if ingame. | |

You can find a good example on how to use these commands [here](https://github.com/3arthqu4ke/HeadlessMc/issues/8#issuecomment-1159378478).
