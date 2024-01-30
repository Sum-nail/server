package backend.sumnail.mock;

import backend.sumnail.domain.nail_shop_hashtag.entity.NailShopHashtag;
import backend.sumnail.domain.nail_shop_hashtag.service.port.NailShopHashtagRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class FakeNailShopHashtagRepository implements NailShopHashtagRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<NailShopHashtag> data = new ArrayList<>();

    @Override
    public List<NailShopHashtag> getByNailShopId(Long id) {
        return data.stream()
                .filter(item -> item.getNailShop().getId().equals(id))
                .toList();
    }

    public void save(NailShopHashtag nailShopHashtag) {
        if (nailShopHashtag.getId() == null || nailShopHashtag.getId() == 0) {
            NailShopHashtag newNailShopHashtag = NailShopHashtag.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .nailShop(nailShopHashtag.getNailShop())
                    .hashtag(nailShopHashtag.getHashtag())
                    .build();
            data.add(newNailShopHashtag);
        } else {
            data.removeIf(it -> Objects.equals(it.getId(), nailShopHashtag.getId()));
            data.add(nailShopHashtag);
        }
    }
}