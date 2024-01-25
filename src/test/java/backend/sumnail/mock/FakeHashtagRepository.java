package backend.sumnail.mock;

import backend.sumnail.domain.hashtag.entity.Hashtag;
import backend.sumnail.domain.hashtag.repository.HashtagRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeHashtagRepository implements HashtagRepository {

    private final AtomicLong id = new AtomicLong(0);
    private final List<Hashtag> data=new ArrayList<>();

    @Override
    public Optional<Hashtag> findByHashtagName(String hashtagName) {
        return data.stream()
                .filter(item->item.getHashtagName().equals(hashtagName))
                .findAny();
    }

    @Override
    public List<Hashtag> findAll() {
        return data;
    }

    @Override
    public Hashtag getById(Long id) {
        return data.stream()
                .filter(item->item.getId().equals(id))
                .findAny()
                .orElseThrow();
    }

    public Hashtag save(Hashtag hashtag){
        if(hashtag.getId()==null||hashtag.getId()==0)
        {
            Hashtag newHashtag=Hashtag.builder()
                    .id(id.get())
                    .hashtagName(hashtag.getHashtagName())
                    .build();
            data.add(newHashtag);
            return newHashtag;
        }
        else{
            data.removeIf(it->it.getId()==hashtag.getId());
            data.add(hashtag);
            return hashtag;
        }
    }
}
