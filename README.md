# forte 🁣
the android launcher that makes your smartphone dumber.  
[now available on google play](https://play.google.com/store/apps/details?id=hunterirving.forte)
  
![](forte.gif)

### a cure for what ails you
ditch your cluttered home screen. keep the necessities.

### robust and elegant
swipe to select one of four built-in applications.

### a surge of deep satisfaction
re-imagine your phone as a tool. rediscover freedom of the mind.

### what's in the box:
• ATLAS, a map of the entire planet (pocket version)  
• CAMERA, a tool for capturing memories (now in color!)  
• ECHO, which converts audible vibrations into radio waves, enabling brain-to-brain psychic communication with anyone in the world  
• HERMES, the same thing but you use your thumbs  

Dmbarrad has added the next ones:  
• MNEMOSYNE, a tool for remembering  
• CHRONOS, an alarm to wake you up before you get robbed  
• EUTERPE, the gods have spoken

### optional accessories (not included):
• daily planner/sketchbook  (done)  
• bedside alarm clock  (done)  
• walkman/mp3 player  (done)  
• pocket calculator    
• gameboy  


### dmbarrad's input: 
Very cool stuff from hunter, I've never did anything related to android apps, so this cost me one night to compile.   
Three apps I use and like:   
CHRONOS is [Sleep as android](https://play.google.com/store/apps/details?id=com.urbandroid.sleep)  
EUTERPE is [AIMP music player](https://play.google.com/store/apps/details?id=com.aimp.player)  
MNEMOSYNE is [PlainText Organizer](https://f-droid.org/packages/de.ferreum.pto/)  

### what I did?
I tried this  
`sudo apt install openjdk-11-jdk`  
downloaded https://developer.android.com/studio#downloads   
`mkdir -p ~/Android/Sdk/cmdline-tools`  
moved all bin/ from studio.tar.gz to cmdline-tools above  
clicked on the studio binary, followed instructions until it downloaded all needed things.  
open terminal in forte's directory  
`chmod +x ./gradlew`  
`./gradlew clean`  
`./gradlew --refresh-dependencies`  
`./gradlew assembleDebug`  
The apk is going to be in ./app/build/outputs/apk/debug as app-debug.apk  
I've uploaded the one I got, look into the files.   

result:
![](screenshot_forte.jpg)

web site  
[hunterirving.com/forte](http://www.hunterirving.com/forte)

🗺️📷☎📃

