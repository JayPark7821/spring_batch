package com.pass.passbatch.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;

import com.pass.passbatch.repository.packaze.PackageEntity;
import com.pass.passbatch.repository.packaze.PackageRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
@ActiveProfiles("test")
public class PackageRepositoryTest {

	@Autowired
	private PackageRepository repository;

	@Test
	void test_save() throws Exception {
		//given
		PackageEntity packageEntity = new PackageEntity();
		packageEntity.setPackageName("바디 챌린지 PT 12주");
		packageEntity.setPeriod(84);
		//when
		repository.save(packageEntity);
		//then
		assertNotNull(packageEntity.getPackageSeq());
	}

	@Test
	void findByCreatedAtAfter() throws Exception {
		//given
		LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(1);

		PackageEntity packageEntity0 = new PackageEntity();
		packageEntity0.setPackageName("학생 전용 3개월");
		packageEntity0.setPeriod(90);
		repository.save(packageEntity0);

		PackageEntity packageEntity1 = new PackageEntity();
		packageEntity1.setPackageName("학생 전용 6개월");
		packageEntity1.setPeriod(180);
		repository.save(packageEntity1);

		//when
		List<PackageEntity> packageEntity = repository.findByCreatedAtAfter(localDateTime,
			PageRequest.of(0, 1, Sort.by("packageSeq").descending()));
		//then
		assertEquals(1, packageEntity.size());
		assertEquals(packageEntity1.getPackageSeq(), packageEntity.get(0).getPackageSeq());
	}

	@Test
	void updateCountAndPeriod() throws Exception {
		//given
		PackageEntity packageEntity = new PackageEntity();
		packageEntity.setPackageName("바디프로필 이벤트 4개월");
		packageEntity.setPeriod(90);
		repository.save(packageEntity);
		//when
		int updatedCount = repository.updateCoundAndPeriod(packageEntity.getPackageSeq(), 30, 120);
		PackageEntity updatedPackageEntity
			= repository.findById(packageEntity.getPackageSeq()).get();
		//then

		assertEquals(1, updatedCount);
		assertEquals(30, updatedPackageEntity.getCount());
		assertEquals(120, updatedPackageEntity.getPeriod());
	}
	
	@Test
	void delete_test () throws Exception {
	    //given
		PackageEntity packageEntity = new PackageEntity();
		packageEntity.setPackageName("제거할 이용권");
		packageEntity.setCount(1);
		PackageEntity savedPackage = repository.save(packageEntity);
		//when
		repository.deleteById(savedPackage.getPackageSeq());

	    //then
		assertTrue(repository.findById(savedPackage.getPackageSeq()).isEmpty());
	}
}
