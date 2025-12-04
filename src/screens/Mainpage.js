import axios from 'axios';
import React, { useEffect, useRef, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  Image,
  TouchableOpacity,
  FlatList,
  Dimensions,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

const screenWidth = Dimensions.get('window').width;
const cardWidth = screenWidth * 0.95;
const cardHeight = cardWidth * 1.2;
const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';
const FILE_BASE_URL = API_BASE_URL.replace('/api/v1', '/files');

export default function Mainpage({ navigation }) {
  const [current, setCurrent] = useState(0);
  const flatRef = useRef(null);

  const [notices, setNotices] = useState([]);
  const [loadingNotices, setLoadingNotices] = useState(false);
  const [error, setError] = useState(null);

  const [exhibitions, setExhibitions] = useState([]);
  const [loadingExhibitions, setLoadingExhibitions] = useState(false);
  const [exhError, setExhError] = useState(null);

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

  useEffect(() => {
    const fetchExhibitions = async () => {
      setLoadingExhibitions(true);
      try {
        const resp = await axios.get(`${API_BASE_URL}/exhibitions`);
        if (resp.data?.success && Array.isArray(resp.data?.data)) {
          setExhibitions(resp.data.data);
          setExhError(null);
        } else {
          setExhibitions([]);
          setExhError('전시회 정보를 불러오지 못했습니다.');
        }
      } catch (e) {
        console.log('전시회 목록 조회 에러:', e);
        setExhibitions([]);
        setExhError('네트워크 에러가 발생했습니다.');
      } finally {
        setLoadingExhibitions(false);
      }
    };

    fetchExhibitions();
  }, []);

  const renderCard = ({ item, index }) => {
    const posterUri = item.imageUrl
      ? `${FILE_BASE_URL}/${item.imageUrl}`
      : null;

    const displayDate = item.date ? item.date.replace(/-/g, '.') : '';

    return (
      <TouchableOpacity
        activeOpacity={0.9}
        style={styles.cardContainer}
        onPress={() =>
          navigation.navigate('ProjectList', { term: item.term }) // ← term으로 이동
        }
      >
        {posterUri ? (
          <Image
            source={{ uri: posterUri }}
            style={styles.cardImage}
            resizeMode="cover"
          />
        ) : (
          <View style={[styles.cardImage, { backgroundColor: '#ddd' }]} />
        )}

        <View style={styles.cardOverlay}>
          <Text style={styles.cardOverlayTitle} numberOfLines={2}>
            {item.title}
          </Text>
          <Text style={styles.cardOverlaySubtitle}>
            {item.date.replace(/-/g, '.')}
          </Text>
          <Text style={styles.cardOverlaySubtitle} numberOfLines={1}>
            {item.place}
          </Text>
        </View>

        {index > 0 && (
          <TouchableOpacity
            style={styles.arrowLeft}
            onPress={() =>
              flatRef.current?.scrollToIndex({ index: index - 1 })
            }
          >
            <Text style={{ fontSize: 24 }}>◀</Text>
          </TouchableOpacity>
        )}
        {index < exhibitions.length - 1 && (
          <TouchableOpacity
            style={styles.arrowRight}
            onPress={() =>
              flatRef.current?.scrollToIndex({ index: index + 1 })
            }
          >
            <Text style={{ fontSize: 24 }}>▶</Text>
          </TouchableOpacity>
        )}
      </TouchableOpacity>
    );
  };


return (
  <SafeAreaView style={styles.container} edges={['top', 'left', 'right']}>
    <FlatList
      data={[{ key: 'dummy' }]}
      keyExtractor={item => item.key}
      renderItem={() => null}
      ListHeaderComponent={
        <>
          <View style={styles.header}>
            <Image
              source={require('./assets/logo.png')}
              style={styles.logo}
            />
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
          <View style={styles.sliderArea}>
            {loadingExhibitions ? (
              <Text>전시회 정보를 불러오는 중...</Text>
            ) : exhError ? (
              <Text style={{ color: 'red' }}>{exhError}</Text>
            ) : exhibitions.length === 0 ? (
              <Text>등록된 전시회가 없습니다.</Text>
            ) : (
              <FlatList
                ref={flatRef}
                data={exhibitions}
                horizontal
                pagingEnabled
                showsHorizontalScrollIndicator={false}
                keyExtractor={item => String(item.id)}
                renderItem={renderCard}
                onMomentumScrollEnd={e => {
                  const index = Math.round(
                    e.nativeEvent.contentOffset.x / cardWidth,
                  );
                  setCurrent(index);
                }}
                snapToInterval={cardWidth}
                decelerationRate="fast"
              />
            )}
          </View>

          {exhibitions.length > 0 && (
            <View style={styles.indicatorArea}>
              {exhibitions.map((_, idx) => (
                <View
                  key={idx}
                  style={[
                    styles.indicatorDot,
                    {
                      backgroundColor:
                        idx === current
                          ? 'rgba(33,33,33,0.88)'
                          : 'rgba(33,33,33,0.22)',
                    },
                  ]}
                />
              ))}
            </View>
          )}

          <View style={styles.newsArea}>
            <Text style={styles.newsTitle}>NEWS</Text>

            {loadingNotices && <Text>불러오는 중...</Text>}
            {error && (
              <Text style={{ color: 'red', marginBottom: 6 }}>{error}</Text>
            )}

            <FlatList
              data={notices}
              keyExtractor={item => String(item.id)}
              renderItem={({ item }) => (
                <TouchableOpacity
                  style={styles.noticeItem}
                  onPress={() =>
                    navigation.navigate('NoticeDetail', {
                      noticeId: item.id,
                    })
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
      contentContainerStyle={{ paddingBottom: 80 }}
    />
  </SafeAreaView>
);
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },

  header: {
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 12,
    marginBottom: 40,
  },
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
    tintColor: '#222',
  },

  logo: {
    width: 100,
    height: 25,
    marginLeft: 16,
    marginTop: 10,
    resizeMode: 'contain',
  },
  titleArea: { marginLeft: 16, marginBottom: 8 },
  title: { fontSize: 20, fontWeight: '800', lineHeight: 30 },

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

  arrowLeft: {
    position: 'absolute',
    left: 4,
    top: '50%',
    marginTop: -14,
    zIndex: 1,
  },
  arrowRight: {
    position: 'absolute',
    right: 4,
    top: '50%',
    marginTop: -14,
    zIndex: 1,
  },

  indicatorArea: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: 10,
  },
  indicatorDot: {
    width: 8,
    height: 8,
    borderRadius: 4,
    backgroundColor: '#333',
    marginHorizontal: 3,
  },

  newsArea: {
    marginTop: 10,
    width: cardWidth,
    height: cardHeight * 0.4,
    paddingHorizontal: 20,
    paddingVertical: 20,
    backgroundColor: 'white',
    alignSelf: 'center',
    borderRadius: 25,
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
