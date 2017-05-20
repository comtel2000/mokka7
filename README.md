# Mokka7
[![Build status](https://travis-ci.org/comtel2000/mokka7.svg?branch=master)](https://travis-ci.org/comtel2000/mokka7)
[![Codacy Badge](https://api.codacy.com/project/badge/grade/6f9977dc35fc4eb1b51328539c7515ea)](https://www.codacy.com/app/comtel2000/mokka7)

## About
Mokka7/Snap7 is an open source, 32/64 bit, multi-platform Ethernet communication suite for interfacing natively with Siemens S7 PLCs. The new CPUs 1200/1500, the old S7200, the small LOGO 0BA7/0BA8 and SINAMICS Drives are also partially supported.
Mokka7 is native port of Snap7 core written in pure Java (fork of moka7 http://snap7.sourceforge.net/) and parts of Sharp7.

Moka7 features:
* Native port of Snap7 core in pure Java, no DLL
* No dependencies with external libraries
* Packed protocol headers to improve performances

additional Mokka7 (fork) features (alpha state):
* DataTypes support (Bit)
* MultiVars Read/Write
* Enhanced API (Java8 required)

## UI Client

![si](https://github.com/comtel2000/mokka7/blob/master/doc/sys_info.png "System Info")

![rw](https://github.com/comtel2000/mokka7/blob/master/doc/read_write.png "Read and Write")

![chart](https://github.com/comtel2000/mokka7/blob/master/doc/chart.png "Chart View")


## Roadmap
* Async non blocking read/write
* MultiVars as collections (automatic slit size)

## Maven Modules

### Mokka7 core (core lib)
```xml
<dependency>
    <groupId>org.comtel2000</groupId>
    <artifactId>mokka7-core</artifactId>
    <version>[LATEST]</version>
</dependency>
```

### Mokka7 metrics (metrics support)
```xml
<dependency>
    <groupId>org.comtel2000</groupId>
    <artifactId>mokka7-metrics</artifactId>
    <version>[LATEST]</version>
</dependency>
```

### Mokka7 client (UI client interface)
```xml
<dependency>
    <groupId>org.comtel2000</groupId>
    <artifactId>mokka7-client</artifactId>
    <version>[LATEST]</version>
</dependency>
```

### Mokka7 samples (sample read/writes)
```xml
<dependency>
    <groupId>org.comtel2000</groupId>
    <artifactId>mokka7-sample</artifactId>
    <version>[LATEST]</version>
</dependency>
```

# License
Eclipse Public License - v 1.0

# Special thanks to
* Dave Nardella for initial API and implementation [Snap7, Moka7, Sharp7](http://snap7.sourceforge.net)