package backend.sumnail.domain.hashtag.service;

import backend.sumnail.domain.hashtag.controller.dto.response.HashtagFindAllResponse;
import backend.sumnail.domain.hashtag.entity.Hashtag;
import backend.sumnail.domain.hashtag.repository.HashtagRepository;
import backend.sumnail.domain.nail_shop.entity.NailShop;
import backend.sumnail.domain.nail_shop_hashtag.service.NailShopHashtagService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class HashtagService {

    private final HashtagRepository hashtagRepository;
    private final NailShopHashtagService nailShopHashtagService;

    public HashtagFindAllResponse findAllHashtag(){
        List<String> list=new ArrayList<>();
        List<Hashtag> hashtags=hashtagRepository.findAll();

        for (Hashtag hashtag:hashtags) {
            list.add(hashtag.getHashtagName());
        }
        HashtagFindAllResponse response=HashtagFindAllResponse.builder()
                .hashtags(list)
                .build();
        return response;
    }

    public List<Hashtag> findHashtags(final NailShop nailShop) {
        List<Hashtag> hashtags = nailShopHashtagService.getByNailShopId(nailShop)
                .stream()
                .map(nailShopHashtag -> hashtagRepository.getById(nailShopHashtag.getHashtag().getId()))
                .toList();

        if (hashtags.size() > Hashtag.MAX_HASHTAG_COUNT) {
            // TODO 커스텀 에러 만든 후 수정
            throw new RuntimeException("해시태그의 최대개수는 3개입니다.");
        }

        return hashtags;
    }
}
