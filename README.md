Joe’s Media Player & DJ’ing App
===============================

| CI Server    | Status
|--------------|--------
| snap-ci      | [![Build Status](https://snap-ci.com/Joe-Merten/Mpjoe/branch/master/build_image)](https://snap-ci.com/Joe-Merten/Mpjoe)
| drone.io     | [![Build Status](https://drone.io/github.com/Joe-Merten/Mpjoe/status.png)](https://drone.io/github.com/Joe-Merten/Mpjoe)
| travis-ci    | [![Build Status](https://travis-ci.org/Joe-Merten/Mpjoe.svg?branch=master)](https://travis-ci.org/Joe-Merten/Mpjoe)
| circleci     | [![Build Status](https://circleci.com/gh/Joe-Merten/Mpjoe.svg)](https://circleci.com/gh/Joe-Merten/Mpjoe)
| circleci     | [![Build Status](https://circleci.com/gh/Joe-Merten/Mpjoe.svg?style=shield)](https://circleci.com/gh/Joe-Merten/Mpjoe)
| semaphoreapp | [![Build Status](https://semaphoreapp.com/api/v1/projects/ed34e48b-8b31-4d78-a3cd-0730d586feaa/341075/badge.png)](https://semaphoreapp.com/joe-merten/mpjoe)
| semaphoreapp | [![Build Status](https://semaphoreapp.com/api/v1/projects/ed34e48b-8b31-4d78-a3cd-0730d586feaa/341075/shields_badge.svg)](https://semaphoreapp.com/joe-merten/mpjoe)


Allgemeines zur Entwicklung
===========================
- zum bauen verwende ich Maven (für alle Unterprojekte)
- bevorzugte IDE ist Eclipse, es kann aber auch jeder andere Editor (vim et cetera) verwendet werden
- durch Verwendung von Maven können alle Projekte auch von Kommandozeile kompiliert werden
- Java 1.7
- Android Api Level 19 (wg. Java 1.7, also mind. Android 4.4 erforderlich)
- CI Anbindung siehe [Ci/README.md](Ci/README.md)


Prerequisites
=============

TODO: Eclipse Setup auch beschreiben


All platforms
-------------
- java jdk 1.7 (or later)
- maven 3.0.5 (or later)
- vlc 2.1.5 (or later)
- android sdk 24.0.2 with build-tools 21.1.2 and api level 19


Linux
-----

        sudo apt-get install git openjdk-7-jdk maven vlc
        [ "$(uname -m)" == "x86_64" ] && sudo apt-get install lib32stdc++6 lib32z1
        git clone https://github.com/Joe-Merten/Mpjoe.git
        Mpjoe/Tools/InstallAndroidSdk.sh

- vlc must be at least 2.1.5
  - it's because of GnuTls issues in vlc 2.1.4
  - for Kubuntu 14.04, I succeed by adding [this ppa](https://launchpad.net/~videolan/+archive/ubuntu/master-daily) which updates my vlc to the most recent 3.0.0
    (yep, it's next to bleeding edge `sudo add-apt-repository ppa:videolan/master-daily`) ... but it seems that this not longer works with ubuntu 14.04 because of some unavailable package dependencies
  - but it could also be used e.g. 2.2.1 from [this ppa](https://launchpad.net/~mc3man/+archive/ubuntu/trusty-media)
    `sudo add-apt-repository ppa:mc3man/trusty-media && sudo apt-get update && sudo apt-get install vlc`
  - Note: Should removing that ppa before performing distribution upgrade `sudo apt-get purge vlc-data && sudo sudo ppa-purge ppa:videolan/master-daily`
- the 32 bit libs were needed for using the android sdk on 64 bit hosts
- there might be some different ways how and where to install android sdk; I'd described one in the script mentioned above
- tested using Kubuntu 14.04 (32 Bit and 64 Bit), OpenJdk 1.7.0_65 and also Kubuntu 14.10 (64 Bit)


OS X
----
- install [Homebrew](http://brew.sh)
- brew update
- brew install maven
- sudo echo "JAVA_HOME=$(/usr/libexec/java_home)" >>/etc/mavenrc
  - because of an maven issue, [see here](http://blog.tompawlak.org/maven-default-java-version-mac-osx) or [here](http://www.jayway.com/2013/03/08/configuring-maven-to-use-java-7-on-mac-os-x/)
  - of course, there are some different approched to fix this issue, e.g.
    - export JAVA_HOME=$(/usr/libexec/java_home) within your .bash_profile et cetera
- install android sdk, e.g.:
  - git clone https://github.com/Joe-Merten/Mpjoe.git
  - Mpjoe/Tools/InstallAndroidSdk.sh
- download & install vlc from https://www.videolan.org
- download & install java from https://www.java.com (e.g. jdk 8 from [here](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html))
- tested using Yosemite 10.10 (64 Bit), Sun Jdk 1.8.0_31


Windows
-------
- TODO


Build & run
===========

Swing app:

    cd Mpjoe/Swing
    mvn clean install
    java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar

Android app:

    cd Mpjoe/Android
    mvn clean install android:deploy android:run


Troubleshooting
===============

Linux 32 Bit & VLC crashes
--------------------------
On my Kubuntu 14.04 32 Bit, I'm able to playback mp3 using vlcj. But I earn crashes when trying to play flac, ogg or video.
Unfortunately, there is no solution until now. See also → https://github.com/caprica/vlcj/issues/300


OSX & VLC - No plugins found
----------------------------
When trying to start the Mpjoe swing application, I' sometimes got an error like »core libvlc error: No plugins found! Check your VLC installation«

Testet e.g. with vlc 2.2.0-rc2 Weatherwax.

Assuming the vlc installation location is `/Applications/VLC.app`, I see files & directories like:

* Contents/MacOS
* Contents/MacOS/lib/libvlc.dylib
* Contents/MacOS/plugins
* Contrnts/vlc -> MacOS

When looking to the whole error output, we see something like `Make sure the plugins are installed in the "<libvlc-path>/vlc/plugins" directory ...`.
Assuming, that `<libvlc-path>` in our case equals to `/Applications/VLC.appContents/MacOS/lib`, then there is of course no `vlc/plugins` below there.

Two workaroundssolutions for that issue:

* Set the VLC_PLUGIN_PATH environment variable like:

        VLC_PLUGIN_PATH=/Applications/VLC.app/Contents/MacOS/plugins java -jar target/Mpjoe-Swing-0.0.1-SNAPSHOT-jar-with-dependencies.jar

* or creating a symlink into the vlc installation:

        mkdir /Applications/VLC.app/Contents/MacOS/lib/vlc
        ln -s ../../plugins /Applications/VLC.app/Contents/MacOS/lib/vlc/plugins


Tips & Tricks
=============

Using Android Virtual Device (Emulator) with HW Acceleration
------------------------------------------------------------

See also:

* https://software.intel.com/de-de/android/articles/speeding-up-the-android-emulator-on-intel-architecture#_Toc358213273
* http://stackoverflow.com/a/12941873
* http://techtach.com/2014/05/boost-android-emulator-performanceon-linux-speeding-up-android-emulator-on-ubuntu/
* http://developer.android.com/tools/devices/emulator.html

### Tested on Kubuntu 14.04 & 14.10 (64 Bit)

```bash
   # check if HW acceleration is available, must return a value > 0
   $ egrep -c '(vmx|svm)' /proc/cpuinfo
   4
   # install kvm
   $ sudo apt-get install qemu-kvm libvirt-bin ubuntu-vm-builder bridge-utils
   # check if kvm is working properly
   $ kvm-ok
   INFO: /dev/kvm exists
   KVM acceleration can be used
   # add user to required groups
   $ sudo adduser $USER kvm
   $ sudo adduser $USER libvirtd
   # now logout the user and login to make change the effect
   # create an avd based on the intel atom image
   $ echo no | android create avd --force -n -My-Emu -t android-19 --abi x86
   $ echo 'hw.keyboard=yes' >>~/.android/avd/My-Emu.avd/config.ini
   # start the new created avd
   $ emulator -avd My-Emu -qemu -m 512 -enable-kvm -gpu on
   # TODO ...
```

* the `android create avd ...` creates a new virtual device below `~/.android/avd`
* the `echo 'hw.keyboard=yes'...` is because of [this issue](http://stackoverflow.com/a/11252510)
* you can also use the avd gui `android avd` to create the virtual device, but be shure to select Cpu/Abi `Intel Atom (x86)`


Android debug over Tcp
----------------------

To debug an Android device wireless (including apk upload etc) we can establish a Tcp connection.

* switch on & unlock the Android device
* connect the device via Usb to the host and allow access
* on the host machine enter `adb tcpip 5555`
* now disconnect Usb
* »connect« the debugger by entering `adb connect <ip-addr-of-your-android-device>:5555` on the host machine (it should respond like »connected to <ip-addr>«)
* now you should be able to perform `mvn android:deploy` etc. and remote debugging without Usb connection
* tested with linux (Kubuntu 14.04) but should also work with Mac
