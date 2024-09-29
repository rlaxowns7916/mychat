package com.example.mychat

import com.example.mychat.storage.redis.PubSubManager
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PubSubManagerTest {
    @Autowired
    lateinit var pubSubManager: PubSubManager

    @Test
    fun `should publish and subscribe to Redis channel`() =
        runBlocking {
            val channelName = "testChannel"
            val messageToSend = "Hello from test"

            // Redis 채널 구독 시작
            val job =
                launch {
                    val sharedFlow = pubSubManager.subscribe(channelName)

                    // 구독을 시작하고 데이터가 올 때마다 처리
                    val messages = mutableListOf<String>()
                    sharedFlow.take(1).collect { message ->
                        messages.add(message)
                    }
                    assertThat(messages).contains(messageToSend)
                }

            // 구독을 시작하고 약간의 딜레이를 준 후 메시지 발행
            delay(100)
            pubSubManager.publish(channelName, messageToSend)

            // 1초 정도 대기 후 구독 종료
            job.join()
            pubSubManager.unsubscribe(channelName)
        }

    @Test
    fun `should_unsubscribe_from_Redis_channel`(): Unit =
        runBlocking {
            val channelName = "testUnsubscribeChannel"
            val messageToSend = "Test Message Before Unsubscribe"

            // 채널 구독
            val sharedFlow = pubSubManager.subscribe(channelName)

            // 구독 시작
            val job =
                launch {
                    sharedFlow.collect {
                        // 메시지 수신 시 취소 전까지 메시지를 받음
                        println("Received message: $it")
                    }
                }

            // 메시지 발행
            pubSubManager.publish(channelName, messageToSend)

            // 구독 취소
            pubSubManager.unsubscribe(channelName)

            // 구독을 취소한 후 추가 메시지를 보내더라도 더 이상 수신되지 않음
            pubSubManager.publish(channelName, "Message After Unsubscribe")

            // 구독 종료
            job.cancelAndJoin()

            // 구독이 취소되었음을 확인
            assertThat(pubSubManager.subscriptions).doesNotContainKey(channelName)
        }
}
