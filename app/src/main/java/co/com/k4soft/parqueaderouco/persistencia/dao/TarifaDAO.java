package co.com.k4soft.parqueaderouco.persistencia.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import co.com.k4soft.parqueaderouco.entidades.Tarifa;

@Dao
public interface TarifaDAO {

    @Insert
    void insert(Tarifa tarifa);

    @Update
    void update(Tarifa tarifa);

    @Delete
    void delete(Tarifa tarifa);

    @Query("DELETE FROM tarifa WHERE idTarifa=:idTarifa")
    void deleteByIdTarifa(Integer idTarifa);

    @Query("SELECT * FROM tarifa")
    List<Tarifa> listar();

    @Query("SELECT * FROM tarifa WHERE idTarifa=:idTarifa")
    Tarifa getByIdTarifa(Integer idTarifa);
}
