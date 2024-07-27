<h1 align="center" style="font-weight: normal;"><b>HMC-Specifics</b></h1>
<p align="center">Version specific implementations of the HeadlessMc runtime.</p>
<p align="center"><a href="https://github.com/3arthqu4ke/mc-runtime-test">Mc-Runtime-Test</a> | <a href="https://github.com/3arthqu4ke/headlessmc">HMC</a> | HMC-Specifics | <a href="https://github.com/3arthqu4ke/hmc-optimizations">HMC-Optimizations</a></p>

<div align="center">

[![CodeFactor](https://www.codefactor.io/repository/github/3arthqu4ke/hmc-specifics/badge/main)](https://www.codefactor.io/repository/github/3arthqu4ke/hmc-specifics/overview/main)
[![GitHub All Releases](https://img.shields.io/github/downloads/3arthqu4ke/hmc-specifics/total.svg)](https://github.com/3arthqu4ke/hmc-specifics/releases)
![GitHub](https://img.shields.io/github/license/3arthqu4ke/hmc-specifics)
![Github last-commit](https://img.shields.io/github/last-commit/3arthqu4ke/hmc-specifics)

</div>

Version specific implementations of the [HeadlessMc](https://github.com/3arthqu4ke/HeadlessMc) runtime. Just place the
jar for your version in your mods folder. Then you need to find a way to send commands to your running Minecraft game.
The easiest way is probably to just launch it with HeadlessMc. The `-commands` flag is not required for HeadlessMc in that case.

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
| connect | Connects you to a server. | \<ip\> \<port\> |
| disconnect | Disconnects you from a server. | |

You can find a good example on how to use these commands [here](https://github.com/3arthqu4ke/HeadlessMc/issues/8#issuecomment-1159378478).

The HMC-Specifics currently support the following Minecraft versions and modloaders:
| Version  | Forge | Fabric | NeoForge |
| :-: | :-: | :-: | :-: |
| 1.21  | :white_check_mark:  | :white_check_mark:  | :white_check_mark: |
| 1.20.6  | :white_check_mark:  | :white_check_mark:  | :white_check_mark: |
| 1.20.1-1.20.4  | :x:  | :x:  | :x:  |
| 1.19.4  | :white_check_mark:  | :white_check_mark:  | - |
| 1.18.2  | :white_check_mark:  | :white_check_mark:  | - |
| 1.17.1  | :white_check_mark:  | :white_check_mark:  | - |
| 1.16.5  | :white_check_mark:  | :white_check_mark:  | - |
| 1.12.2  | :white_check_mark:  | :warning:  | - |
| 1.8.9  | :white_check_mark:  | :warning:  | - |
| 1.7.10  | :white_check_mark:  | :warning:  | - |

Versions marked with :warning: have not been tested yet, due to not being supported by HeadlessMC, e.g. fabric legacy versions.
