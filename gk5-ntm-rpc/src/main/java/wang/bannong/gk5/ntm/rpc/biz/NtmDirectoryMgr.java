package wang.bannong.gk5.ntm.rpc.biz;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import wang.bannong.gk5.ntm.common.domain.NtmDirectory;
import wang.bannong.gk5.ntm.common.dto.NtmDirectoryDto;
import wang.bannong.gk5.ntm.common.model.NtmResult;
import wang.bannong.gk5.ntm.common.model.PaginationResult;
import wang.bannong.gk5.ntm.common.vo.NtmDirectoryVo;
import wang.bannong.gk5.ntm.rpc.dao.NtmDirectoryDao;

@Component
public class NtmDirectoryMgr {

    @Autowired
    private NtmDirectoryDao masterNtmDirectoryDao;
    @Autowired
    private NtmApiMgr       ntmApiMgr;


    public NtmResult list(Long pid) throws Exception {
        List<NtmDirectory> list = masterNtmDirectoryDao.selectByPid(pid);
        if (CollectionUtils.isNotEmpty(list)) {
            List<NtmDirectory> listChildren = masterNtmDirectoryDao.selectByPids(list
                    .stream()
                    .map(NtmDirectory::getId)
                    .collect(Collectors.toList()));
            Map<Long, Boolean> map = new HashMap<>();
            if (CollectionUtils.isNotEmpty(listChildren)) {
                Map<Long, List<NtmDirectory>> map1 = listChildren.stream().collect(Collectors.groupingBy(NtmDirectory::getPid));
                for (Map.Entry<Long, List<NtmDirectory>> entry : map1.entrySet()) {
                    map.put(entry.getKey(), CollectionUtils.isNotEmpty(entry.getValue()));
                }
            }

            List<NtmDirectoryVo> vos = new ArrayList<>();
            for (NtmDirectory item : list) {
                NtmDirectoryVo vo = new NtmDirectoryVo();
                vo.setId(String.valueOf(item.getId()));
                vo.setPid(String.valueOf(item.getPid()));
                vo.setName(item.getName());
                vo.setSort(item.getSort());
                if (map.get(item.getId())) {
                    vo.setHasChildren(true);
                } else {
                    vo.setHasChildren(false);
                }
                vos.add(vo);
            }

            return NtmResult.success(PaginationResult.of(1, list.size(), 1, list.size(), vos));
        }

        return NtmResult.success(PaginationResult.empty(1, 10));
    }

    @Transactional
    public NtmResult add(NtmDirectoryDto dto) throws Exception {
        Long pid = dto.getPid();
        NtmDirectory record = masterNtmDirectoryDao.selectByIdAndName(pid, dto.getName());
        if (record != null) {
            return NtmResult.fail("目录已存在");
        }
        record.setPid(dto.getPid());
        record.setName(dto.getName());
        record.setSort(dto.getSort());
        masterNtmDirectoryDao.insert(record);
        return NtmResult.SUCCESS;
    }

    @Transactional
    public NtmResult modify(NtmDirectoryDto dto) throws Exception {
        Long id = dto.getId();
        NtmDirectory record = masterNtmDirectoryDao.selectById(id);
        NtmDirectory record1 = masterNtmDirectoryDao.selectByIdAndName(record.getPid(), dto.getName());
        if (record1 != null) {
            return NtmResult.fail("目录已存在");
        }
        record.setName(dto.getName());
        if (dto.getSort() != null) {
            record.setSort(dto.getSort());
        }
        masterNtmDirectoryDao.updateById(record);
        return NtmResult.SUCCESS;
    }

    @Transactional
    public NtmResult del(NtmDirectoryDto dto) throws Exception {
        Long id = dto.getId();
        List<NtmDirectory> records = masterNtmDirectoryDao.selectByPid(id);
        if (CollectionUtils.isNotEmpty(records)) {
            return NtmResult.fail("存在下级目录，请先处理");
        }

        if (CollectionUtils.isNotEmpty(ntmApiMgr.queryByDictId(id))) {
            return NtmResult.fail("目录存在绑定的接口，请先处理");
        }
        masterNtmDirectoryDao.deleteById(id);
        return NtmResult.SUCCESS;
    }
}
