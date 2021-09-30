# Spring R Socket Client Example

## 개요

- Spring Webflux에서 R Socket을 다루는 예시 코드입니다.  
  R Socket을 통해 이벤트를 주고 받을 때, 이 애플리케이션은 클라이언트로부터 요청을 받아  
  이벤트를 발행하는 부분에 해당합니다.

- 발행된 이벤트를 처리하는 Consumer에 해당하는 코드는 <a href="https://github.com/Example-Collection/Spring_RSocket_Server_Example">여기</a>에 있습니다.

- R Socket 패러다임에 맞게, 총 4개의 REST API가 포함되어 있습니다.

  - _요청-응답_ : `[POST] /items/request-response`
  - _요청-스트림_ : `[GET] /items/request-stream`
  - _실행 후 망각_ : `[POST] /items/fire-and-forget`
  - _채널_ : `[GET] /items`

- 이 애플리케이션은 8081번 포트에서 구동됩니다.

<hr/>

## 실행 방법

- 이 애플리케이션이 제공하는 REST API와 테스트 코드가 통과하려면, <a href="https://github.com/Example-Collection/Spring_RSocket_Server_Example">R Socket Server</a>가  
  실행되어 있어야 하며, 두 애플리케이션은 동일한 MongoDB를 사용해야 합니다.

- 가장 간단하게, 아래 명령어로 Docker에 Mongo Container를 띄우면 데모용으로는 충분합니다.

```sh
docker run -p 27017-27019:27017-27019 mong
```

<hr/>

## 참고 사항

- `build.gradle.kts`를 보면, 아래 의존성이 추가되어 있습니다.

```gradle
//..

dependencies {
    runtimeOnly("io.netty:netty-resolver-dns-native-macos:4.1.68.Final:osx-aarch_64")

    //..
}
```

- 위 패키지는 Netty가 MacOS에서 로컬 DNS를 해석하는 데에 도움을 주는 패키지 입니다.  
  `osx-aarch_64`인 이유는 M1 Mac을 사용하기 때문이며, 만약 MacOS의 Intel 버전 또는  
  다른 OS를 사용한다면, 알맞은 패키지를 지정해주면 됩니다.

> 참고: <a href="https://github.com/netty/netty/issues/11020">Github Issue</a>

<>
