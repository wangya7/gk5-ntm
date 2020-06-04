package wang.bannong.gk5.ntm.rpc.dao;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import wang.bannong.gk5.normdb.mongo.MongoManager;
import wang.bannong.gk5.ntm.common.bo.NtmTracesBo;
import wang.bannong.gk5.ntm.common.constant.ApiConfig;
import wang.bannong.gk5.ntm.common.model.GroupModel;
import wang.bannong.gk5.ntm.common.model.NtmTraces;
import wang.bannong.gk5.util.DateUtils;


/**
 * Created by bn. on 2019/10/11 5:03 PM
 */
@Repository
public class NtmTracesDao {

    public static final String KEY = "ntm.traces";

    @Autowired
    private MongoManager mongoManager;

    public void insert(NtmTraces bo) throws Exception {
        bo.setCreateMs(System.currentTimeMillis());
        bo.setCreateTime(DateUtils.format(new Date()));
        mongoManager.insert(bo, KEY);
    }

    public List<NtmTraces> query(NtmTracesBo bo) throws Exception {
        Query query = buildQuery(bo);
        int pageNum = bo.getPageNum(), pageSize = bo.getPageSize();
        long skip = (pageNum - 1) * pageSize;
        query.with(new Sort(Direction.DESC, ApiConfig.CREATE_MS))
             .skip(skip).limit(pageSize);

        return mongoManager.find(query, NtmTraces.class, KEY);
    }

    public long count(NtmTracesBo bo) throws Exception {
        return mongoManager.count(buildQuery(bo), KEY);
    }

    public Query buildQuery(NtmTracesBo bo) throws Exception {
        Query query = new Query();
        if (StringUtils.isNotBlank(bo.getApi())) {
            Criteria criteria = new Criteria(ApiConfig.API);
            criteria.is(bo.getApi());
            query.addCriteria(criteria);
        }

        if (bo.getApiId() > 0) {
            Criteria criteria = new Criteria(ApiConfig.API_ID);
            criteria.is(bo.getApiId());
            query.addCriteria(criteria);
        }

        if (StringUtils.isNotBlank(bo.getAppid())) {
            Criteria criteria = new Criteria(ApiConfig.APPID);
            criteria.is(bo.getAppid());
            query.addCriteria(criteria);
        }

        if (StringUtils.isNotBlank(bo.getChannel())) {
            Criteria criteria = new Criteria(ApiConfig.CHANNEL);
            criteria.is(bo.getChannel());
            query.addCriteria(criteria);
        }

        if (StringUtils.isNotBlank(bo.getIp())) {
            Criteria criteria = new Criteria(ApiConfig.IP);
            criteria.is(bo.getIp());
            query.addCriteria(criteria);
        }

        if (StringUtils.isNotBlank(bo.getIa())) {
            Criteria criteria = new Criteria(ApiConfig.IA);
            criteria.is(bo.getIa());
            query.addCriteria(criteria);
        }

        if (bo.getCode() > -1) {
            Criteria criteria = new Criteria(ApiConfig.CODE);
            criteria.is(bo.getCode());
            query.addCriteria(criteria);
        }

        if (bo.getResultCode() != null) {
            Criteria criteria = new Criteria(ApiConfig.CODE);
            if (bo.getResultCode()) {
                criteria.is(0);
            } else {
                criteria.gt(0);
            }
            query.addCriteria(criteria);
        }

        if (bo.getRequestBeginTime() != null || bo.getRequestEndTime() != null) {
            Criteria criteria = new Criteria(ApiConfig.TS);
            if (bo.getRequestBeginTime() != null)
                criteria.gte(bo.getRequestBeginTime().getTime());
            if (bo.getRequestEndTime() != null)
                criteria.lte(bo.getRequestEndTime().getTime());
            query.addCriteria(criteria);
        }

        if (bo.getCreateBeginTime() != null || bo.getCreateEndTime() != null) {
            Criteria criteria = new Criteria(ApiConfig.CREATE_MS);
            if (bo.getCreateBeginTime() != null)
                criteria.gte(bo.getCreateBeginTime().getTime());
            if (bo.getCreateEndTime() != null)
                criteria.lte(bo.getCreateEndTime().getTime());
            query.addCriteria(criteria);
        }
        return query;
    }

    public List<GroupModel> groupApiWithTime(NtmTracesBo bo) throws Exception {
        int pageNum = bo.getPageNum(), pageSize = bo.getPageSize();
        long skip = (pageNum - 1) * pageSize;

        // AggregationOperation matchOperation = new MatchOperation(criteria); 不推荐使用 直接使用 Aggregation.match
        // AggregationOperation groupOperation = new GroupOperation(Fields.fields(ApiConfig.API)); 同样不推荐使用

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(buildGroupApiWithTime(bo)),
                // 开发文档 https://docs.mongodb.com/manual/reference/operator/aggregation/group/
                Aggregation.group(ApiConfig.API)
                           .count().as(GroupModel.MODEL_TOTAL)
                           .first(ApiConfig.API).as(GroupModel.MODEL_NAME)
                           .max(ApiConfig.API_ID).as(GroupModel.MODEL_ID),
                Aggregation.sort(new Sort(Direction.DESC, GroupModel.MODEL_TOTAL)),
                Aggregation.skip(skip),
                Aggregation.limit(pageSize));
        return mongoManager.aggregate(aggregation, KEY, GroupModel.class)
                           .getMappedResults();
    }

    public long countGroupApiWithTime(NtmTracesBo bo) throws Exception {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(buildGroupApiWithTime(bo)),
                Aggregation.group(ApiConfig.API)
                           .count().as(GroupModel.MODEL_TOTAL)
                           .first(ApiConfig.API).as(GroupModel.MODEL_NAME)
                           .max(ApiConfig.API_ID).as(GroupModel.MODEL_ID));
        List<GroupModel> results = mongoManager.aggregate(aggregation, KEY, GroupModel.class).getMappedResults();
        return CollectionUtils.isNotEmpty(results) ? results.size() : 0;
    }

    public Criteria buildGroupApiWithTime(NtmTracesBo bo) throws Exception {
        Criteria criteria = new Criteria();
        if (bo.getRequestBeginTime() != null && bo.getRequestEndTime() != null) {
            criteria.andOperator(Criteria.where(ApiConfig.TS).gte(bo.getRequestBeginTime().getTime()).lte(bo.getRequestEndTime().getTime()));
        } else if (bo.getRequestBeginTime() != null && bo.getRequestEndTime() == null) {
            criteria.andOperator(Criteria.where(ApiConfig.TS).gte(bo.getRequestBeginTime().getTime()));
        } else if (bo.getRequestBeginTime() == null && bo.getRequestEndTime() != null) {
            criteria.andOperator(Criteria.where(ApiConfig.TS).lte(bo.getRequestEndTime().getTime()));
        }

        if (bo.getCreateBeginTime() != null && bo.getCreateEndTime() != null) {
            criteria.andOperator(Criteria.where(ApiConfig.CREATE_MS).gte(bo.getCreateBeginTime().getTime()).lte(bo.getCreateEndTime().getTime()));
        } else if (bo.getCreateBeginTime() != null && bo.getCreateEndTime() == null) {
            criteria.andOperator(Criteria.where(ApiConfig.CREATE_MS).gte(bo.getCreateBeginTime().getTime()));
        } else if (bo.getCreateBeginTime() == null && bo.getCreateEndTime() != null) {
            criteria.andOperator(Criteria.where(ApiConfig.CREATE_MS).lte(bo.getCreateEndTime().getTime()));
        }
        return criteria;
    }


}
