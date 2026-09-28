package company.backend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import company.backend.entity.PortfolioProject;
import company.backend.repo.PortfolioProjectRepository;

import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
class PortfolioDataSeederTest {

	@Mock
	private PortfolioProjectRepository portfolioProjectRepository;

	@InjectMocks
	private PortfolioDataSeeder seeder;

	@Test
	void seedsSampleProjectsWhenTheTableIsEmpty() {
		when(this.portfolioProjectRepository.count()).thenReturn(0L);
		when(this.portfolioProjectRepository.saveAll(any())).thenAnswer(invocation -> invocation.getArgument(0));

		this.seeder.run(new DefaultApplicationArguments());

		// ArgumentCaptor.captor() infers the element type, so there is no unchecked
		// cast from the raw List.class that ArgumentCaptor.forClass() would need.
		ArgumentCaptor<List<PortfolioProject>> captor = ArgumentCaptor.captor();
		verify(this.portfolioProjectRepository).saveAll(captor.capture());
		assertThat(captor.getValue()).hasSize(3).allSatisfy(project -> {
			assertThat(project.isSampleProject()).isTrue();
			assertThat(project.getSlug()).isNotBlank();
			assertThat(project.getProblem()).isNotBlank();
			assertThat(project.getSolution()).isNotBlank();
		});
	}

	@Test
	void leavesExistingContentAlone() {
		when(this.portfolioProjectRepository.count()).thenReturn(7L);

		this.seeder.run(new DefaultApplicationArguments());

		verify(this.portfolioProjectRepository, never()).saveAll(any());
	}
}
