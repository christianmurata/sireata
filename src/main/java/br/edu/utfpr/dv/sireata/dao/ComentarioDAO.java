package br.edu.utfpr.dv.sireata.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import br.edu.utfpr.dv.sireata.model.Comentario;
import br.edu.utfpr.dv.sireata.model.Comentario.SituacaoComentario;

public class ComentarioDAO {
	
	public Comentario buscarPorId(int id) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{
			connectionDao.queryParam("SELECT * FROM comentarios WHERE idComentario = ?");
			connectionDao.setInt(1, id);
			
			ResultSet rs = connectionDao.execute();
			
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public Comentario buscarPorUsuario(int idUsuario, int idPauta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT comentarios.*, usuarios.nome AS nomeUsuario FROM comentarios " +
				"INNER JOIN usuarios ON usuarios.idUsuario=comentarios.idUsuario " +
				"WHERE comentarios.idPauta=" + String.valueOf(idPauta) + " AND comentarios.idUsuario=" + String.valueOf(idUsuario));
		
			if(rs.next()){
				return this.carregarObjeto(rs);
			}else{
				return null;
			}
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public List<Comentario> listarPorPauta(int idPauta) throws SQLException{
		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			ResultSet rs = connectionDao.query().executeQuery("SELECT comentarios.*, usuarios.nome AS nomeUsuario FROM comentarios " +
				"INNER JOIN usuarios ON usuarios.idUsuario=comentarios.idUsuario " +
				"WHERE comentarios.idPauta=" + String.valueOf(idPauta) + " ORDER BY usuarios.nome");
		
			List<Comentario> list = new ArrayList<Comentario>();
			
			while(rs.next()){
				list.add(this.carregarObjeto(rs));
			}
			
			return list;
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	public int salvar(Comentario comentario) throws SQLException{
		boolean insert = (comentario.getIdComentario() == 0);

		ConnectionDAO connectionDao = ConnectionDAO.getInstance();
		
		try{		
			if(insert){
				connectionDao.queryParam("INSERT INTO comentarios(idPauta, idUsuario, situacao, comentarios, situacaoComentarios, motivo) VALUES(?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			}else{
				connectionDao.queryParam("UPDATE comentarios SET idPauta=?, idUsuario=?, situacao=?, comentarios=?, situacaoComentarios=?, motivo=? WHERE idComentario=?");
			}
			
			connectionDao.setInt(1, comentario.getPauta().getIdPauta());
			connectionDao.setInt(2, comentario.getUsuario().getIdUsuario());
			connectionDao.setInt(3, comentario.getSituacao().getValue());
			connectionDao.setString(4, comentario.getComentarios());
			connectionDao.setInt(5, comentario.getSituacaoComentarios().getValue());
			connectionDao.setString(6, comentario.getMotivo());
			
			if(!insert){
				connectionDao.setInt(7, comentario.getIdComentario());
			}
			
			connectionDao.getPreparedStatement().execute();
			
			if(insert){
				ResultSet rs = connectionDao.getPreparedStatement().getGeneratedKeys();
				
				if(rs.next()){
					comentario.setIdComentario(rs.getInt(1));
				}
			}
			
			return comentario.getIdComentario();
		} catch (SQLException sqlException) {
			throw new SQLException(sqlException);
		}
	}
	
	private Comentario carregarObjeto(ResultSet rs) throws SQLException{
		Comentario comentario = new Comentario();
		
		comentario.setIdComentario(rs.getInt("idComentario"));
		comentario.getPauta().setIdPauta(rs.getInt("idPauta"));
		comentario.getUsuario().setIdUsuario(rs.getInt("idUsuario"));
		comentario.getUsuario().setNome(rs.getString("nomeUsuario"));
		comentario.setSituacao(SituacaoComentario.valueOf(rs.getInt("situacao")));
		comentario.setComentarios(rs.getString("comentarios"));
		comentario.setSituacaoComentarios(SituacaoComentario.valueOf(rs.getInt("situacaoComentarios")));
		comentario.setMotivo(rs.getString("motivo"));
		
		return comentario;
	}

}
