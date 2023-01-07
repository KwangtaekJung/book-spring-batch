package com.example.chapter7.util;

import com.example.chapter7.domain.CustomerWithTransactions;
import com.example.chapter7.domain.Transaction;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.Resource;

import java.util.ArrayList;


public class CustomerMultiFileReader implements ResourceAwareItemReaderItemStream<CustomerWithTransactions> {

    private Object curItem = null;

    private ResourceAwareItemReaderItemStream<Object> delegate;

    public CustomerMultiFileReader(ResourceAwareItemReaderItemStream<Object> delegate) {
        this.delegate = delegate;
    }

    public CustomerWithTransactions read() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
        }

        CustomerWithTransactions item = (CustomerWithTransactions) curItem;
        curItem = null;

        if (item != null) {
            item.setTransactions(new ArrayList<>());

            while (peek() instanceof Transaction) {
                item.getTransactions().add((Transaction) curItem);
                curItem = null;
            }
        }
        return item;
    }

    public Object peek() throws Exception {
        if (curItem == null) {
            curItem = delegate.read();
        }
        return curItem;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        delegate.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        delegate.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        delegate.close();
    }

    @Override
    public void setResource(Resource resource) {
        this.delegate.setResource(resource);
    }
}
