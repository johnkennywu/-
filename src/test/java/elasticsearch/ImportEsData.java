package elasticsearch;

import com.gf.intelligence.BootApiApplication;
import com.gf.intelligence.constant.Constants;
import com.gf.intelligence.dto.Question;
import com.gf.intelligence.service.GFDataService;
import com.gf.intelligence.util.ElasticSearchClient;
import com.gf.intelligence.util.ExcelReadUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wushubiao
 * @Title: ImportEsData
 * @ProjectName gf-intelligence
 * @Description:
 * @date 2019/10/10
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes= BootApiApplication.class)
public class ImportEsData {
    @Autowired
    ElasticSearchClient esClient;
    @Autowired
    GFDataService gfDataService;

    @Test
    public void run() {
        try{
            List<String[]> list =  ExcelReadUtil.readExcel(Constants.GF_DATA_PATH);
            List<Question> questions = new ArrayList<Question>();
            for(String[] arr : list) {
                Question q = new Question();
                q.setQuestion(arr[0]);
                q.setAnswer(arr[1]);
                q.setKeyword(arr[2]);
                q.setClicks("0");
                questions.add(q);
            }
            gfDataService.bulkIndex(Constants.GF_INDEX, Constants.GF_TYPE, esClient, questions);
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
