package com.yondu.knowledgebase.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.yondu.knowledgebase.entities.Rating;

public interface RatingRepository extends JpaRepository<Rating, Long>{
	
	@Query(value = "SELECT EXISTS( SELECT * FROM rating WHERE user_id=:user_id AND entity_id=:entity_id AND entity_type=:entity_type AND is_active=1 ) ", nativeQuery = true)
    public Long isRecordExistAndActive(Long user_id, Long entity_id, String entity_type);
	
	@Query(value = "SELECT EXISTS( SELECT * FROM rating WHERE user_id=:user_id AND entity_id=:entity_id AND entity_type=:entity_type AND is_active=0 ) ", nativeQuery = true)
    public Long isRecordExistAndInactive(Long user_id, Long entity_id, String entity_type);
	
	@Query(value = "SELECT * FROM rating WHERE user_id=:user_id AND entity_id=:entity_id AND entity_type=:entity_type", nativeQuery = true)
    public Rating findByUserIdEntityIdEntityType(Long user_id, Long entity_id, String entity_type);
	
	@Query(value = "SELECT ( SELECT COUNT(*) FROM rating WHERE entity_id=:entity_id AND entity_type=:entity_type AND is_active=1 AND rating='UP' )"
					+ " - ( SELECT COUNT(*) FROM rating WHERE entity_id=:entity_id AND entity_type=:entity_type AND is_active=1 AND rating='DOWN' )", nativeQuery = true)
    public int countUpvoteByEntityIdAndEntityType(Long entity_id, String entity_type);
	
	@Query(value = "SELECT COUNT(*) FROM rating WHERE entity_id=:entity_id AND entity_type=:entity_type AND is_active=1", nativeQuery = true)
	public int countTotalVoteByEntityIdAndEntityType(Long entity_id, String entity_type);
}
