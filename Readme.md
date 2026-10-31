![Hackatime badge
](https://hackatime.hackclub.com/api/v1/badge/U0ALZQ9FP9P/By-Koy/Forgery)
> Forgery is currently in the POC phase, most features have not yet been implemented
# Forgery
A minecraft mod that adds more ores and more things to do with them.

Forgery is a tinker's construct inspired mod that adds more ores, special effects, and overall more things to disrupt the usual ore / tool / armour progression.

The mod has a few core parts:
### The Forgery
The forgery is the main structure the mod progresses with. it consists of blackstone (or other blocks that can survive the heat), a melter, a campfire, a nozzle and a cast.

### The Melter
The melter is, as the name suggests, the block that melts ores into melted fluid. you can feed it up to three ores, and it will create a fluid from those ingots.\
On its own, the melter is useless, you need a campfire under it (both normal and soul work!) to provide heat for the reaction. You also need something to use the melted fluid with.

### The Nozzle And Cast
In come the outputs. The nozzle is just a translator between melted fluid and the cast itself. it's just there to let the fluid out. \
The cast on the other hand, is the heart of the operation - it's used to choose what you want to create. this is done by adding stencils, which are little squares that create the chosen output.

## The Caveat
As you may have noticed (by the massive warning or whatever), this mod is in the poc phase and thus most features aren't done. How not done? Here's a list of features: (the checkboxes signify status) \
[x] - A working melter \
[ ] - fully functional melted fluid \
[x] - a (mostly) working nozzle \
[ ] - literally any stencil (you can make a block at least :cryin:) \
[x] - a single, not complete ore (at least it spawns naturally!) \
[x] - a creative mode tab to host all the mod's items. \

## Testing
Now that we got past the boring part, here's how you start!

### If You Own Minecraft
Simply download the mod from the [releases page](https://github.com/By-Koy/Forgery/releases) and run it with the fabric launcher as you would any other!

### Or If You Don't
> Disclaimer: please do not use this method for anything but testing / developing mods. Minecraft is a paid game,

Clone this repo, and in a terminal (in the repo's folder!) run the following commands:

Linux / MacOS:
```bash
./gradlew runClient
```

Windows:
```cmd
gradlew.bat runClient
```

## Credits And Legal
This project is (not) protected by the UnLicence, please check out [the licence file](https://github.com/By-Koy/Forgery/blob/main/LICENSE) for more info.

Thank you to HackClub and specifically the Stardance team for giving me the motivation to create this mod (kind of)

Made with love, by Koy💜