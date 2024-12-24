package com.yupi.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.yupi.springbootinit.common.BaseResponse;
import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import com.yupi.springbootinit.exception.ThrowUtils;
import com.yupi.springbootinit.manager.AIManger;
import com.yupi.springbootinit.manager.RedisLimiter;
import com.yupi.springbootinit.model.entity.Chart;
import com.yupi.springbootinit.model.vo.BIResponse;
import com.yupi.springbootinit.service.ChartService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.yupi.springbootinit.utils.ExcelUtils.excelToCsv;

@Slf4j
@Component
public class BIMessageConsumer {
    @Resource
    private ChartService chartService;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private RedisLimiter redisLimiter;
    @Resource
    private AIManger aiManger;
    @SneakyThrows
    @RabbitListener(queues = {BIMqConstant.BI_QUEUE},ackMode = "MANUAL")
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {

        if(StringUtils.isBlank(message)){
            channel.basicNack(deliveryTag,false,false);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"消息为空");
        }
        Chart chart = chartService.getById(Long.parseLong(message));
       if(chart==null){
           channel.basicNack(deliveryTag,false,false);
           throw new BusinessException(ErrorCode.SYSTEM_ERROR,"图表为空");
       }

        log.info("receiveMessage message = {}", message);

        long chartId = Long.parseLong(message);
        Chart updateChart = new Chart();
        updateChart.setId(chart.getId());
        updateChart.setStatus("running");
        boolean updateresult = chartService.updateById(updateChart);
        if(!updateresult){
            channel.basicNack(deliveryTag,false,false);
            handleChartResult(chart.getId(),"更新图表运行状态失败");
            return ;
        }
        String result = aiManger.sendMsgToXingHuo(true, buildUserInput(chart));
        String[] splits = result.split("'【【【【【'");
        if (splits.length < 3) {
            channel.basicNack(deliveryTag,false,false);
            handleChartResult(chart.getId(),"AI 生成错误");
            return ;
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chart.getId());
        updateChartResult.setStatus("succeed");
        updateChartResult.setGenChart(genChart);
        updateChartResult.setGenResult(genResult);
        boolean updated = chartService.updateById(updateChartResult);
        if(!updated){
            channel.basicNack(deliveryTag,false,false);
            handleChartResult(chart.getId(),"更新图表成功状态失败");
            return ;
        }

        log.info("receiveMessage message = {}", message);
        channel.basicAck(deliveryTag,false);

    }
    private void handleChartResult(long chartId, String execMessage) {
        Chart updateChartResult = new Chart();
        updateChartResult.setId(chartId);
        updateChartResult.setStatus("failed");
        boolean updated = chartService.updateById(updateChartResult);
        if(!updated){
            log.error("更新图表失败状态失败"+ chartId+execMessage);
        }
    }

    /**
     * 构建用户输入
     * @param chart
     * @return
     */
    private String buildUserInput(Chart chart){
        //用户输入
        StringBuilder userinput = new StringBuilder();
        userinput.append("分析需求：").append("\n");
        String goal = chart.getGoal();
        String chartType = chart.getChartType();
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal = goal + ",请使用" + chartType;
        }
        userinput.append(userGoal).append("\n");
        //压缩后的数据
        String csvData = chart.getChartData();
        userinput.append("原始数据：").append("\n").append(csvData).append("\n");
        return userinput.toString();
    }


}