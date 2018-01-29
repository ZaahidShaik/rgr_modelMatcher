package ubc.pavlab.rdp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Transient;
import ubc.pavlab.rdp.listeners.UserEntityListener;
import ubc.pavlab.rdp.model.enums.TierType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


@Entity
@Table(name = "user")
@EntityListeners(UserEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString( of = {"id", "email", "enabled"})
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "user_id")
    @JsonIgnore
	private int id;

	@Column(name = "email")
	@Email(message = "*Please provide a valid Email")
	@NotEmpty(message = "*Please provide an email")
	private String email;

	@Column(name = "password")
	@Length(min = 6, message = "*Your password must have at least 6 characters")
	@NotEmpty(message = "*Please provide your password")
    @JsonIgnore
	@Transient
	private String password;

	@Column(name = "enabled")
    @JsonIgnore
	private boolean enabled;

	@ManyToMany
	@JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @JsonIgnore
	private Set<Role> roles = new HashSet<>();

	@Embedded
    @JsonUnwrapped
    private Profile profile;

	/* Research related information */

	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "user_id")
	private Set<TaxonDescription> taxonDescriptions = new HashSet<>();

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "user_id")
	private Set<UserTerm> userTerms = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "user_id")
	private Set<UserGene> userGenes = new HashSet<>();

    @JsonIgnore
    @Transient
    public Set<Gene> getGenes() {
        return this.getUserGenes().stream().map( UserGene::getGene ).collect( Collectors.toSet() );
    }

    @JsonIgnore
    @Transient
    public Set<GeneOntologyTerm> getTerms() {
        return this.userTerms.stream().map( UserTerm::getTerm ).collect( Collectors.toSet() );
    }

    @JsonIgnore
    @Transient
    public Set<Gene> getGenesByTaxon( Taxon taxon ) {
        return this.getUserGenes().stream().map( UserGene::getGene )
                .filter( gene -> gene.getTaxon().equals( taxon ) ).collect( Collectors.toSet() );
    }

    @JsonIgnore
    @Transient
    public Set<Gene> getGenesByTaxonAndTier( Taxon taxon, Set<TierType> tiers) {
        return this.getUserGenes().stream()
                .filter( ga -> ga.getGene().getTaxon().equals( taxon ) && tiers.contains( ga.getTier()) )
                .map( UserGene::getGene ).collect( Collectors.toSet() );
    }

    @JsonIgnore
    @Transient
    public Set<UserGene> getGenesAssociationsByTaxon( Taxon taxon ) {
        return this.getUserGenes().stream().filter( ga -> ga.getGene().getTaxon().equals( taxon ) ).collect( Collectors.toSet() );
    }

    @JsonIgnore
    @Transient
    public Set<GeneOntologyTerm> getTermsByTaxon( Taxon taxon ) {
        return this.getUserTerms().stream().filter( term -> term.getTaxon().equals( taxon ) ).map(UserTerm::getTerm).collect( Collectors.toSet() );
    }
}
