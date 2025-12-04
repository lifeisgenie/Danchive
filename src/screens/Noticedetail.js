import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ActivityIndicator,
  ScrollView,
  TouchableOpacity,
} from 'react-native';
import axios from 'axios';

const API_BASE_URL = 'http://100.84.161.55:8080/api/v1';

const NoticeDetail = ({ route, navigation }) => {
  const { noticeId } = route.params;

  const [notice, setNotice] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

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
    const fetchDetail = async () => {
      setLoading(true);
      try {
        const resp = await axios.get(`${API_BASE_URL}/notices/${noticeId}`);
        if (resp.data?.success && resp.data?.data) {
          setNotice(resp.data.data);
          setError('');
        } else {
          setError('공지사항을 불러오지 못했습니다.');
        }
      } catch (e) {
        console.log('공지 상세 조회 에러:', e);
        setError('네트워크 에러가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    fetchDetail();
  }, [noticeId]);

  if (loading) {
    return (
      <View style={styles.center}>
        <ActivityIndicator size="small" />
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.center}>
        <Text style={{ color: 'red', marginBottom: 8 }}>{error}</Text>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={{ color: '#295cae' }}>뒤로가기</Text>
        </TouchableOpacity>
      </View>
    );
  }

  if (!notice) {
    return (
      <View style={styles.center}>
        <Text>공지사항 정보가 없습니다.</Text>
      </View>
    );
  }

  return (
    <View style={styles.container}>
      {/* 상단 헤더 */}
      <View style={styles.header}>
        <TouchableOpacity onPress={() => navigation.goBack()}>
          <Text style={styles.backText}>{'‹'}</Text>
        </TouchableOpacity>
        <Text style={styles.headerTitle}>공지사항</Text>
        <View style={{ width: 24 }} />
      </View>

      <ScrollView contentContainerStyle={styles.contentWrap}>
        <Text style={styles.title}>{notice.title}</Text>

        <View style={styles.metaRow}>
          <Text style={styles.metaText}>{notice.createdByName}</Text>
          <Text style={styles.metaDivider}>·</Text>
          <Text style={styles.metaText}>{formatDate(notice.createdAt)}</Text>
        </View>

        <Text style={styles.body}>
          {notice.content}
        </Text>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#fff' },
  center: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#fff',
  },
  header: {
    height: 48,
    flexDirection: 'row',
    alignItems: 'center',
    marginTop: 20,
    paddingHorizontal: 16,
    borderBottomWidth: 0.5,
    borderBottomColor: '#eee',
  },
  backText: {
    fontSize: 22,
    color: '#222',
    width: 24,
    textAlign: 'left',
  },
  headerTitle: {
    flex: 1,
    textAlign: 'center',
    fontSize: 16,
    fontWeight: '600',
    color: '#222',
  },
  contentWrap: {
    paddingHorizontal: 20,
    paddingVertical: 16,
  },
  title: {
    fontSize: 18,
    fontWeight: '700',
    marginBottom: 8,
    color: '#111',
  },
  metaRow: {
    flexDirection: 'row',
    alignItems: 'center',
    marginBottom: 16,
  },
  metaText: {
    fontSize: 12,
    color: '#888',
  },
  metaDivider: {
    fontSize: 12,
    color: '#ccc',
    marginHorizontal: 6,
  },
  body: {
    fontSize: 14,
    color: '#333',
    lineHeight: 20,
  },
});

export default NoticeDetail;
