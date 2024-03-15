# 게시글 리스트 조회 성능 테스트를 위한 더미 데이터

insert into member (id)
values (1);

delimiter $$
drop procedure if exists loopinsert$$

create procedure loopinsert()
begin
    declare i INT default 1;
    declare sp VARCHAR(10);
    declare rs varchar(50);

    while i <= 50000
        do
            set sp = IF(rand() < 0.5, 'DOG', 'CAT');
            set rs = if(rand() < 0.33, 'PUBLISHED', if(rand() < 0.67, 'DELETED', 'FOUND'));
            insert into report(age, latitude, longitude, shows_phone, member_id, views, description,
                               feature, pet_name, road_address, species, street_address, title,
                               pet_type, report_status)
            values (1, round(36.0 + rand() * (37.9 - 36.0), 5),
                    round(126.0 + rand() * (127.9 - 126.0), 5), false, 1, 0, 'description',
                    '검정색 털을 가지고 있음', '멍뭉이', 'road', '말티즈', 'street', '우리집 강아지를 찾아주세요..!!', sp,
                    rs);
            set i = i + 1;
        end while;

    set i = 1;
    while i <= 50000
        do
            set sp = IF(rand() < 0.5, 'DOG', 'CAT');
            set rs = if(rand() < 0.33, 'PUBLISHED', if(rand() < 0.67, 'DELETED', 'FOUND'));
            insert into report(age, latitude, longitude, shows_phone, member_id, views, description,
                               feature, pet_name, road_address, species, street_address, title,
                               pet_type, report_status)
            values (1, round(36.0 + rand() * (37.9 - 36.0), 5),
                    round(126.0 + rand() * (127.9 - 126.0), 5), false, 1, 0, 'description',
                    '검정색 털을 가지고 있음', '하양이', 'road', '시바견', 'street', '우리집 고양이를 찾아주세요..!!', sp,
                    rs);
            set i = i + 1;
        end while;

    set i = 1;
    while i <= 50000
        do
            set sp = IF(rand() < 0.5, 'DOG', 'CAT');
            set rs = if(rand() < 0.33, 'PUBLISHED', if(rand() < 0.67, 'DELETED', 'FOUND'));
            insert into report(age, latitude, longitude, shows_phone, member_id, views, description,
                               feature, pet_name, road_address, species, street_address, title,
                               pet_type, report_status)
            values (1, round(36.0 + rand() * (37.9 - 36.0), 5),
                    round(126.0 + rand() * (127.9 - 126.0), 5), false, 1, 0, 'description',
                    '검정색 털을 가지고 있음', '먼지', 'road', '코리안 숏 헤어', 'street', '우리집 냠냐미를 찾아주세요..!!', sp,
                    rs);
            set i = i + 1;
        end while;

    set i = 1;
    while i <= 50000
        do
            set sp = IF(rand() < 0.5, 'DOG', 'CAT');
            set rs = if(rand() < 0.33, 'PUBLISHED', if(rand() < 0.67, 'DELETED', 'FOUND'));
            insert into report(age, latitude, longitude, shows_phone, member_id, views, description,
                               feature, pet_name, road_address, species, street_address, title,
                               pet_type, report_status)
            values (1, round(36.0 + rand() * (37.9 - 36.0), 5),
                    round(126.0 + rand() * (127.9 - 126.0), 5), false, 1, 0, 'description',
                    '검정색 털을 가지고 있음', '하양이', 'road', '푸들', 'street', '우리집 동물을 찾아주세요..!!', sp,
                    rs);
            set i = i + 1;
        end while;
end $$
delimiter $$

call loopinsert();
# call loopinsert();
# call loopinsert();
# call loopinsert();
# call loopinsert();
