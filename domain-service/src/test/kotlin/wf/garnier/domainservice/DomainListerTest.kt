package wf.garnier.domainservice

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import wf.garnier.domain.Domain

class DomainListerTest {
    val domainLister = DomainLister(DomainServiceConfiguration(mutableListOf("com", "org", "io")))

    @Test
    fun `it lists domains`() {
        val expectedDomains = listOf(
            Domain("test", "com", true),
            Domain("test", "org", true),
            Domain("test", "io", true)
        )
        val domains = domainLister.getDomains("test")

        assertThat(domains).isEqualTo(expectedDomains)
    }
}

@RunWith(SpringRunner::class)
@WebMvcTest(DomainServiceConfiguration::class, DomainLister::class)
class DomainListerControllerTest {

    private val expectedDomains = listOf(
        Domain("test", "com", true),
        Domain("test", "org", true),
        Domain("test", "io", true)
    )

    @Autowired
    lateinit var mvc: MockMvc

    @Test
    fun `has correct domains`() {
        mvc.perform(get("/api/domains?search=test"))
            .andExpect(jsonPath(".[*].name").value(expectedDomains.map { it.name }))
            .andExpect(jsonPath(".[*].extension").value(expectedDomains.map { it.extension }))
            .andExpect(jsonPath(".[*].available").value(expectedDomains.map { it.available }))
    }
}