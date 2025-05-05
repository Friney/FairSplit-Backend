package com.friney.fairsplit.core.mapper;

import com.friney.fairsplit.api.dto.event.EventDto;
import com.friney.fairsplit.api.dto.receipt.ReceiptDto;
import com.friney.fairsplit.core.entity.event.Event;
import com.friney.fairsplit.core.entity.receipt.Receipt;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-05T18:27:50+0700",
    comments = "version: 1.6.3, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.11.1.jar, environment: Java 21.0.3 (Eclipse Adoptium)"
)
@Component
public class EventMapperImpl implements EventMapper {

    @Autowired
    private ReceiptMapper receiptMapper;

    @Override
    public EventDto map(Event event) {
        if ( event == null ) {
            return null;
        }

        EventDto.EventDtoBuilder eventDto = EventDto.builder();

        eventDto.id( event.getId() );
        eventDto.name( event.getName() );
        eventDto.description( event.getDescription() );
        eventDto.receipts( receiptSetToReceiptDtoList( event.getReceipts() ) );

        return eventDto.build();
    }

    @Override
    public List<EventDto> map(List<Event> events) {
        if ( events == null ) {
            return null;
        }

        List<EventDto> list = new ArrayList<EventDto>( events.size() );
        for ( Event event : events ) {
            list.add( map( event ) );
        }

        return list;
    }

    protected List<ReceiptDto> receiptSetToReceiptDtoList(Set<Receipt> set) {
        if ( set == null ) {
            return null;
        }

        List<ReceiptDto> list = new ArrayList<ReceiptDto>( set.size() );
        for ( Receipt receipt : set ) {
            list.add( receiptMapper.map( receipt ) );
        }

        return list;
    }
}
