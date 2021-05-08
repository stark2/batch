package com.trxmon.batch.configuration;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

public class CleanupTask implements Tasklet {

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception
    {
        System.out.println("Clean-up start..");

        Integer count = (Integer) chunkContext.getStepContext().getJobExecutionContext().get("count");
        Integer count2 = (Integer) chunkContext.getStepContext().getJobExecutionContext().get("aaa");

        System.out.println("In step 2: step 1 wrote " + count + " items, " + count2);

        System.out.println("Clean-up done..");
        return RepeatStatus.FINISHED;
    }

}
