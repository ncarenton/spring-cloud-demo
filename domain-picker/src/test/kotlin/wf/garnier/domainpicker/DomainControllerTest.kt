package wf.garnier.domainpicker

import com.nhaarman.mockito_kotlin.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wf.garnier.domain.Domain
import wf.garnier.domain.DomainListResponse

class DomainControllerTest {
    private val expectedDomainResponse = DomainListResponse(
            domains = listOf(
                    Domain("example", "com", true),
                    Domain("example", "io", false),
                    Domain("example", "net", false),
                    Domain("example", "org", true)
            )
    )

    private val domainMock: DomainServiceClient = mock {
        on { listDomains(any()) } doReturn expectedDomainResponse
    }

    private val pricingMock: PricingServiceClient = mock {
        on { price(any()) } doReturn 42
    }

    @Test
    fun `it calls the domain service client`() {
        val controller = DomainController(domainMock, mock())

        controller.getAll("example")
        verify(domainMock, times(1)).listDomains("example")
    }

    @Test
    fun `it calls the pricing service client for available domains`() {
        val controller = DomainController(domainMock, pricingMock)

        controller.getAll("example")
        argumentCaptor<String>().apply {
            verify(pricingMock, times(2)).price(capture())
            assertThat(allValues).containsExactlyInAnyOrder("example.com", "example.org")
        }
    }

    @Test
    fun `it returns relevant data`() {
        val controller = DomainController(domainMock, pricingMock)

        val domains = controller.getAll("example")

        val expectedDomains = listOf(
                AugmentedDomain("example", "com", true, null, 42),
                AugmentedDomain("example", "io", false, null, 0),
                AugmentedDomain("example", "net", false, null, 0),
                AugmentedDomain("example", "org", true, null, 42)
        )
        assertThat(domains).isEqualTo(expectedDomains)
    }
}