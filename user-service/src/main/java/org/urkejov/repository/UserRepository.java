package org.urkejov.repository;

import org.urkejov.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
}
