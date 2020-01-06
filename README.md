# fermion
Grondag's semi-public general-purpose library for MC mods.  It is divided into several modules for `include` purposes but I distribute it on curseforge as a single jar file. 

You are welcome to use it if it helps but I'm not promoting most of it as a public-facing API beause docs and code quality are inconsistent in some parts and it can change rapidly.  It was and is developed primarily for my own use. 

[Mod Keys](https://github.com/grondag/fermion/wiki/Fermion-Modifier-Keys) is an exception. It is stable and I will support it if you use it as a dependency.

Other parts of Fermion may become "officially" supported in the future. 


# Build setup

Add my terrible, bad, crappy maven repo to your repostiories section in `build.gradle`

```gradle
repositories {
  maven {
    name = "grondag"
    url = "https://grondag-repo.appspot.com"
    credentials {
      username "guest"
      password ""
    }
  }
}
```

Then include whichever modules you need.  Generally you always want to include the base fermion module - it doesn't do anything but is the parent mod for the others and allows them to be consolidated in ModMenu.

```gradle
  modRuntime "grondag:fermion:${project.fermion_version}.+"
  include "grondag:fermion:${project.fermion_version}.+"
    
  modImplementation ("grondag:fermion-modkeys:${project.fermion_modkeys_version}.+") {
    exclude group :"grondag"
    exclude group :"net.fabricmc.fabric-api"
  }
  include "grondag:fermion-modkeys:${project.fermion_modkeys_version}.+"
    
  modImplementation ("grondag:fermion-varia:${project.fermion_varia_version}.+") {
    exclude group :"grondag"
    exclude group :"net.fabricmc.fabric-api"
  }
  include "grondag:fermion-varia:${project.fermion_varia_version}.+"
    
  modImplementation ("grondag:fermion-simulator:${project.fermion_simulator_version}.+") {
    exclude group :"grondag"
    exclude group :"net.fabricmc.fabric-api"
  }
  include "grondag:fermion-simulator:${project.fermion_simulator_version}.+"
    
  modImplementation ("grondag:special-circumstances:${project.special_circumstances_version}.+") {
    exclude group :"grondag"
    exclude group :"net.fabricmc.fabric-api"
  }
  include "grondag:special-circumstances:${project.special_circumstances_version}.+"
```

Versions are available for both 1.14 and 1.15. Look at [gradle.properties](https://github.com/grondag/fermion/blob/master/gradle.properties) in the appropriate branch to identify latest major.minor versions.
