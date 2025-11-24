import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import { View, Text, StyleSheet, Image, TouchableOpacity, FlatList, Dimensions } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
// useNavigation import는 안 써도 되지만 놔둬도 무방
// import { useNavigation } from '@react-navigation/native';

const screenWidth = Dimensions.get('window').width;
const cardWidth = screenWidth * 0.95;
const cardHeight = cardWidth * 1.2;
const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

const images = [
  require('./assets/sample-photo1.png'),
  require('./assets/sample-photo2.png'),
  require('./assets/sample-photo3.png'),
];

const cardInfos = [
  {
    title: 'Communication Design\nThe 35TH Graduation',
    date: '2024.10.31 ~ 11.04',
  },
];

export default function Mainpage({ navigation }) {
  const [current, setCurrent] = useState(0);
  const flatRef = useRef(null);
  const [notices, setNotices] = useState([]);
  const [loadingNotices, setLoadingNotices] = useState(false);
  const [error, setError] = useState(null);

  // createdAt -> 2025-11-20 형태로 포맷
  const formatDate = iso => {
    if (!iso) return '';
    try {
      const d = new Date(iso);
      const y = d.getFullYear();
      const m = String(d.getMonth() + 1).padStart(2, '0');
      const day = String(d.getDate()).padStart(2, '0');
      return `${y}-${m}-${day}`;
    } catch {
      return iso;
    }
  };

  useEffect(() => {
    const fetchNotices = async () => {
      setLoadingNotices(true);
      try {
        // 전체 허용이므로 토큰 없이 호출
        const response = await axios.get(`${API_BASE_URL}/notices`);

        if (response.data?.success && response.data?.data) {
          setNotices(response.data.data);
          setError(null);
        } else {
          setError('공지사항을 불러오는데 실패했습니다.');
        }
      } catch (e) {
        console.log('공지 목록 조회 에러:', e);
        setError('네트워크 에러가 발생했습니다.');
      } finally {
        setLoadingNotices(false);
      }
    };

    fetchNotices();
  }, []);


  const renderCard = ({ item, index }) => (
    <View style={styles.cardContainer}>
      <Image source={item} style={styles.cardImage} resizeMode="stretch" />
      <View style={styles.cardOverlay}>
        <Text style={styles.cardOverlayTitle}>{cardInfos[index]?.title}</Text>
        <Text style={styles.cardOverlaySubtitle}>{cardInfos[index]?.date}</Text>
        <TouchableOpacity style={styles.cardCopyBtn}>
          <Text>📋</Text>
        </TouchableOpacity>
      </View>
      {index > 0 && (
        <TouchableOpacity
          style={styles.arrowLeft}
          onPress={() => flatRef.current.scrollToIndex({ index: index - 1 })}
        >
          <Text style={{ fontSize: 24 }}>◀</Text>
        </TouchableOpacity>
      )}
      {index < images.length - 1 && (
        <TouchableOpacity
          style={styles.arrowRight}
          onPress={() => flatRef.current.scrollToIndex({ index: index + 1 })}
        >
          <Text style={{ fontSize: 24 }}>▶</Text>
        </TouchableOpacity>
      )}
    </View>
  );

  return (
    <SafeAreaView style={styles.container} edges={['top', 'left', 'right']}>
      <FlatList
        data={[{ key: 'dummy' }]} // 위아래 스크롤 위해 dummy 데이터 1개
        keyExtractor={item => item.key}
        renderItem={() => null} // 실제 아이템 렌더 X
        ListHeaderComponent={
          <>
            <View style={styles.header}>
              <Image source={require('./assets/logo.png')} style={styles.logo} />
              <TouchableOpacity
                style={styles.alarmButton}
                onPress={() => navigation.navigate('NotificationList')}
              >
                <Image
                  source={require('./assets/notification.png')}
                  style={styles.alarmIcon}
                  resizeMode="contain"
                />
              </TouchableOpacity>
            </View>
            <View style={styles.titleArea}>
              <Text style={styles.title}>
                2025학년도{'\n'}소프트웨어학과 졸업작품전시회 :
              </Text>
            </View>
            <View style={styles.sliderArea}>
              <FlatList
                ref={flatRef}
                data={images}
                horizontal
                pagingEnabled
                showsHorizontalScrollIndicator={false}
                keyExtractor={(item, idx) => idx.toString()}
                renderItem={renderCard}
                onMomentumScrollEnd={e => {
                  const index = Math.round(e.nativeEvent.contentOffset.x / cardWidth);
                  setCurrent(index);
                }}
                snapToInterval={cardWidth}
                decelerationRate="fast"
              />
            </View>
            <View style={styles.indicatorArea}>
              {images.map((_, idx) => (
                <View
                  key={idx}
                  style={[
                    styles.indicatorDot,
                    {
                      backgroundColor:
                        idx === current ? 'rgba(33,33,33,0.88)' : 'rgba(33,33,33,0.22)',
                    },
                  ]}
                />
              ))}
            </View>
            <View style={styles.newsArea}>
              <Text style={styles.newsTitle}>NEWS</Text>

              {loadingNotices && <Text>불러오는 중...</Text>}
              {error && <Text style={{ color: 'red', marginBottom: 6 }}>{error}</Text>}

              <FlatList
                data={notices}
                keyExtractor={item => String(item.id)}
                renderItem={({ item }) => (
                  <TouchableOpacity
                    style={styles.noticeItem}
                    onPress={() =>
                      navigation.navigate('NoticeDetail', { noticeId: item.id })
                    }
                  >
                    <View style={styles.noticeRow}>
                      <Text style={styles.noticeTitle} numberOfLines={1}>
                        {item.title}
                      </Text>
                      <Text style={styles.noticeDate}>
                        {formatDate(item.createdAt)}
                      </Text>
                    </View>
                  </TouchableOpacity>
                )}
                style={{ maxHeight: 160 }}
                showsVerticalScrollIndicator={true}
              />
            </View>
          </>
        }
        contentContainerStyle={{ paddingBottom: 40 }}
      />
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  header: { flexDirection: 'row', alignItems: 'center', marginTop: 12, marginBottom: 8 },
  alarmButton: {
    position: 'absolute',
    right: 18,
    top: 2,
    padding: 6,
    zIndex: 2,
  },
  alarmIcon: {
    width: 26,
    height: 26,
    tintColor: '#222', // 필요 시 색상 조정
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 12,
    marginBottom: 8,
    // position: 'relative' 기본(알람 버튼 오른쪽에 배치)
  },

  logo: { width: 46, height: 25, marginLeft: 16 },
  titleArea: { marginLeft: 16, marginBottom: 8 },
  title: { fontSize: 20, fontWeight: 800, lineHeight: 30 },
  sliderArea: { alignItems: 'center', marginBottom: 16 },
  cardContainer: {
    width: cardWidth,
    height: cardHeight,
    borderRadius: 25,
    backgroundColor: 'lightgray',
    overflow: 'hidden',
    marginHorizontal: 8,
    alignItems: 'center',
    justifyContent: 'center',
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 10,
    elevation: 4,
    marginBottom: 8,
  },
  cardImage: {
    width: '100%',
    height: '100%',
    borderRadius: 25,
  },
  cardOverlay: {
    position: 'absolute',
    left: 18,
    bottom: 18,
    right: 18,
    backgroundColor: 'rgba(255,255,255,0.83)',
    borderRadius: 16,
    padding: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.07,
    shadowRadius: 6,
    elevation: 2,
  },
  cardOverlayTitle: { fontSize: 17, fontWeight: 'bold' },
  cardOverlaySubtitle: { fontSize: 13, fontWeight: '400', marginTop: 3 },
  cardCopyBtn: { position: 'absolute', right: 10, bottom: 10 },
  arrowLeft: { position: 'absolute', left: 4, top: '50%', marginTop: -14, zIndex: 1 },
  arrowRight: { position: 'absolute', right: 4, top: '50%', marginTop: -14, zIndex: 1 },
  indicatorArea: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 10,
  },
  indicatorDot: { width: 8, height: 8, borderRadius: 4, backgroundColor: '#333', marginHorizontal: 3 },
  newsArea: {
    marginTop: 10,
    width: cardWidth,
    height: cardHeight * 0.4,
    paddingHorizontal: 20,
    paddingVertical: 20,
    backgroundColor: 'white',
    alignSelf: 'center',
    borderRadius: 25,
    alignself: 'center',
    elevation: 4,
    shadowColor: '#000',
    shadowOffset: { width: 2, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 10,
  },
  newsTitle: { fontSize: 17, fontWeight: 'bold', marginBottom: 10 },
  noticeItem: {
    paddingVertical: 8,
    borderBottomWidth: 0.5,
    borderColor: '#eee',
  },
  noticeRow: {
    flexDirection: 'row',
    alignItems: 'center',
  },
  noticeTitle: {
    flex: 1,
    fontSize: 14,
    color: '#222',
    marginRight: 8,
  },
  noticeDate: {
    fontSize: 12,
    color: '#888',
  },
});