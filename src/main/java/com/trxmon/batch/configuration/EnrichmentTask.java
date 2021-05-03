package com.trxmon.batch.configuration;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class EnrichmentTask implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        System.out.println("EnrichmentTask start..");

        Integer count = (Integer) chunkContext.getStepContext().getJobExecutionContext().get("count");
        System.out.println("In step 2: step 1 wrote " + count + " items");

        System.out.println("EnrichmentTask done..");
        return RepeatStatus.FINISHED;
    }

}
