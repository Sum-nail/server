package backend.sumnail.mock;

import backend.sumnail.domain.nail_shop.entity.NailShop;
import backend.sumnail.domain.user.entity.User;
import backend.sumnail.domain.user_nail_shop.entity.UserNailShop;
import backend.sumnail.domain.user_nail_shop.service.port.UserNailShopRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class FakeUserNailShopRepository implements UserNailShopRepository {
    private final AtomicLong autoGeneratedId = new AtomicLong(0);
    private final List<UserNailShop> data = new ArrayList<>();

    @Override
    public List<UserNailShop> findByUserId(long userId) {
        return data.stream()
                .filter(item -> item.getUser().getId().equals(userId))
                .toList();
    }

    @Override
    public void save(UserNailShop userNailShop) {
        if (userNailShop.getId() == null || userNailShop.getId() == 0) {
            UserNailShop newUserNailShop = UserNailShop.builder()
                    .id(autoGeneratedId.incrementAndGet())
                    .nailShop(userNailShop.getNailShop())
                    .user(userNailShop.getUser())
                    .build();
            data.add(newUserNailShop);
        } else {
            data.removeIf(item -> Objects.equals(item.getId(), userNailShop.getId()));
            data.add(userNailShop);
        }

    }

    @Override
    public void deleteByUserAndNailShop(User user, NailShop nailShop) {
        data.removeIf(item -> item.getUser().equals(user) && item.getNailShop().equals(nailShop));
    }

    @Override
    public Optional<UserNailShop> findByUserAndNailShop(User user, NailShop nailShop) {
        return data.stream()
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .filter(item -> item.getNailShop().getId().equals(nailShop.getId()))
                .findAny();
    }
}
