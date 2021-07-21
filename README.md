## Intel 8080 Emulator Using Java

This is an Intel 8080 emulator built using Java to learm about CPUs and Architecture.

Tests Passing:

- [x] TST8080
- [x] 8080PRE


### Update:

I noticed that some people have taken interest in this project and have forked the repo. I really love and appretiate the interest in Emulation and this project and therefore would like to give some suggestions and recommendations.

I worked on this project around 2 years ago for my own learning and understanding and so I'd like to admit that I haven't really followed the *best* approaches while building this emulator. I even ran into weird bugs at times (Emulator would pass CPU tests but wouldnt run space-invaders proeprly) which I didnt fix until a year later and I believe this project might have some more of those weird bugs.

Java imo isnt really a good language to build Emulators to start with, you have to hande overflow and underflow on your own which other languages would have handled on their own. There is also a lack of unsigned integers except char (uint32, uint16 etc) which can and will cause weird bugs in the code.

I highly recommend checking out the Intel 8080 emulator I build using [rust](https://github.com/dramikei/rust_8080.git). Here I've split each operation into a separate function and have tried to reuse code as much as possible. This made the code a lot more readable and maintainable at the cost of tiny complexity (some functions might be weirdly overloaded which might not be intuitive).

In the Rust 8080 emulator I have learnt from the mistakes I made in this project and therefore Its built better and from scratch.

Additionaly I also recommend checking out the following resources -

[Computer Archeology](http://www.computerarcheology.com)

[Emulator101](http://www.emulator101.com/reference/8080-by-opcode.html)

[alexandrejanin](https://github.com/alexandrejanin/rust-8080)

[Roysten](https://github.com/Roysten/rust-8080)

