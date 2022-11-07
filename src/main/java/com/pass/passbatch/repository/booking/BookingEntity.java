package com.pass.passbatch.repository.booking;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.pass.passbatch.repository.BaseEntity;
import com.pass.passbatch.repository.user.UserEntity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "booking")
public class BookingEntity extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 기본 키 생성을 DB에 위임합니다. (AUTO_INCREMENT)
	private Integer bookingSeq;
	private Integer passSeq;
	private String userId;

	@Enumerated(EnumType.STRING)
	private BookingStatus status;
	private boolean usedPass;
	private boolean attended;

	private LocalDateTime startedAt;
	private LocalDateTime endedAt;
	private LocalDateTime cancelledAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "userId", insertable = false, updatable = false)
	private UserEntity userEntity;
}
