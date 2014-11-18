package module.dao;

import module.dao.impl.BaseDaoImpl;

import org.springframework.stereotype.Repository;

@Repository(DaoFactory.SPECIAL_CRITERIA_DAO)
public class SpecialCriteriaDaoImpl extends BaseDaoImpl implements SpecialCriteriaDao
{
}
