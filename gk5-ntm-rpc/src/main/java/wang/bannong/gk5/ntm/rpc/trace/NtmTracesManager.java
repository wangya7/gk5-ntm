package wang.bannong.gk5.ntm.rpc.trace;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import wang.bannong.gk5.ntm.common.bo.NtmTracesBo;
import wang.bannong.gk5.ntm.common.domain.NtmApi;
import wang.bannong.gk5.ntm.common.model.GroupModel;
import wang.bannong.gk5.ntm.common.model.NtmRequest;
import wang.bannong.gk5.ntm.common.model.NtmResponse;
import wang.bannong.gk5.ntm.common.model.NtmTraces;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.rpc.dao.NtmTracesDao;

/**
 * 日志持久化
 * Created by bn. on 2019/10/11 4:55 PM
 */
@Slf4j
@Component
public class NtmTracesManager {

    @Autowired
    private NtmTracesDao ntmTracesDao;

    public enum method {
        log, rmdb, mongodb;
    }

    public int persistence(NtmRequest request, NtmResponse response, NtmApi ntmApi) throws Exception {
        return persistenceMongodb(request, response, ntmApi);
    }

    public int persistenceFile(NtmRequest request, NtmResponse response, NtmApi ntmApi) throws Exception {
        return 0;
    }

    public int persistenceRmdb(NtmRequest request, NtmResponse response, NtmApi ntmApi) throws Exception {
        return 0;
    }

    public int persistenceMongodb(NtmRequest request, NtmResponse response, NtmApi ntmApi) throws Exception {
        ntmTracesDao.insert(NtmTraces.of(request, response, ntmApi));
        return 0;
    }

    /**
     * 根据条件查询接口列表
     *
     * @param bo
     * @return
     * @throws Exception
     */
    public PaginationResult<List<NtmTraces>> queryApi(NtmTracesBo bo) throws Exception {
        int pageNum = bo.getPageNum(), pageSize = bo.getPageSize();
        long total = ntmTracesDao.count(bo);
        int pages = 0;
        if (total == 0) {
            return PaginationResult.empty(pageNum, pageSize);
        } else {
            if (total % pageSize == 0) {
                pages = (int) (total / pageSize);
            } else {
                pages = (int) (total / pageSize) + 1;
            }
        }
        return PaginationResult.of(pageNum, pageSize, pages, total, ntmTracesDao.query(bo));
    }

    /**
     * 统计一点时间内接口调用的次数，按照总次数降序排列
     *
     * @param bo
     * @return
     * @throws Exception
     */
    public PaginationResult<List<GroupModel>> collectGroupApiWithTime(NtmTracesBo bo) throws Exception {
        int pageNum = bo.getPageNum(), pageSize = bo.getPageSize();
        long total = ntmTracesDao.countGroupApiWithTime(bo);
        int pages = 0;
        if (total == 0) {
            return PaginationResult.empty(pageNum, pageSize);
        } else {
            if (total % pageSize == 0) {
                pages = (int) (total / pageSize);
            } else {
                pages = (int) (total / pageSize) + 1;
            }
        }
        return PaginationResult.of(pageNum, pageSize, pages, total, ntmTracesDao.groupApiWithTime(bo));
    }

    /**
     * 统计单一接口调用正常/异常次数
     *
     * @param bo
     * @return
     * @throws Exception
     */
    public Map<Boolean, Long> collectOneApi(NtmTracesBo bo) throws Exception {
        bo.setPageNum(1);
        bo.setPageSize(Integer.MAX_VALUE);
        List<NtmTraces> list = ntmTracesDao.query(bo);
        long succ = 0, fail = 0;
        if (CollectionUtils.isNotEmpty(list)) {
            for (NtmTraces item : list) {
                if (item.getCode() == 0) {
                    succ++;
                } else {
                    fail++;
                }
            }
        }
        Map<Boolean, Long> map = new HashMap<>(5);
        map.put(true, succ);
        map.put(false, fail);
        return map;
    }
}
