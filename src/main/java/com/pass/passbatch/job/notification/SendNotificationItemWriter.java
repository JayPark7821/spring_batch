package com.pass.passbatch.job.notification;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.pass.passbatch.adapter.message.KakaoTalkMessageAdapter;
import com.pass.passbatch.repository.notification.NotificationEntity;
import com.pass.passbatch.repository.notification.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SendNotificationItemWriter implements ItemWriter<NotificationEntity> {
	private final NotificationRepository notificationRepository;
	private final KakaoTalkMessageAdapter kakaoTalkMessageAdapter;

	@Override
	public void write(List<? extends NotificationEntity> notificationEntities) throws Exception {
		int count = 0;

		for (NotificationEntity notificationEntity : notificationEntities) {
			boolean successful = kakaoTalkMessageAdapter.sendKakaoTalkMessage(notificationEntity.getUuid(),
				notificationEntity.getText());

			if (successful) {
				notificationEntity.setSent(true);
				notificationEntity.setSentAt(LocalDateTime.now());
				notificationRepository.save(notificationEntity);
				count++;
			}
		}
		log.info("SendNorificationItemWriter -write : 수업전 알림 {}/{}건 전송 성공", count, notificationEntities.stream());
	}
}
