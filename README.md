# AdaptiveHuff
该工程是自适应(动态)huffman的编码解码工程，适用于预先不知道各种符号的出现频率，随着字符的不断出现对各个字符的编码集进行动态的调整。

[自适应huffman树讲解](https://blog.csdn.net/weixin_43838265/article/details/117324663)

> 目前支持A-Z的编码和解码,如果需要扩展，可在类AdaptiveHuff中函数initCode添加对应的字符初始编码集。

该工程是maven工程，目的在于使用日志框架，对于动态调整过程可以查看日志记录。同时也可打包成jar包，便于文件的编码和解码。

```bash
1. 编码
java -jar huff.jar -type=encode -srcpath=input.txt -distpath=res.txt
其中input.txt为需要编码的文件，res.txt为编码后的文件
2.解码
java -jar huff.jar -type=decode -srcpath=res.txt -distpath=dist.txt
其中res.txt为需要解码的文件，dist.txt为解码后的文件
```

